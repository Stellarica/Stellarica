package net.stellarica.server.transfer

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.stellarica.server.StellaricaServer
import net.stellarica.server.StellaricaServer.Companion.klogger
import net.stellarica.server.util.Tasks
import net.stellarica.server.util.extension.toBlockPos
import net.stellarica.server.util.extension.toLocation
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.world.WorldLoadEvent
import org.bukkit.persistence.PersistentDataType
import kotlin.math.min

object PipeHandler : Listener {
	const val maxConnectionLength = 16
	const val nodeCapacity = 10

	private val nodes = mutableMapOf<World, MutableMap<BlockPos, Node>>()
	operator fun get(world: World): MutableMap<BlockPos, Node> {
		return nodes.getOrPut(world) { mutableMapOf() }
	}

	init {
		Tasks.syncRepeat(20, 20) {
			for (world in nodes.keys) {
				validateConnections(world)
				tickPipes(world)
			}
		}
	}

	private fun tickPipes(world: World) {
		for ((_, node) in nodes[world] ?: mutableMapOf()) {
			val connections = node.connections.mapNotNull { nodes[world]!![it] }

			// get demand from connected nodes
			val demanding = connections.filter { it.content < node.content }
			if (demanding.isEmpty()) continue

			val sum = demanding.sumOf { it.content }
			val average = sum / demanding.count()

			// pain and suffering because it means that the order in which
			// demanding nodes are iterated through can change how much fuel they get
			// but it works well enough:tm:
			var available = node.content - average

			for (other in demanding) {
				val transfer = min(
					min(
						node.content - other.content, // this node's deficit
						other.capacity - other.content // the other's empty space
					),
					available
				).coerceAtLeast(0)
				available -= transfer
				other.inputBuffer += transfer
				node.outputBuffer += transfer
			}
		}

		// apply changes at once
		for ((_, node) in nodes[world] ?: mutableMapOf()) {
			node.content += node.inputBuffer
			node.content -= node.outputBuffer
			node.inputBuffer = 0
			node.outputBuffer = 0
		}
	}

	private fun validateConnections(world: World) {
		nodes[world] ?: return

		nodes[world] = nodes[world]!!.filter {
			val loc = it.key.toLocation(world)
			!loc.isChunkLoaded || loc.block.type == Material.CUT_COPPER
		}.toMutableMap()

		for ((pos, node) in nodes[world]!!.toMap()) {
			// if the chunk isn't loaded just assume it's valid
			if (!pos.toLocation(world).isChunkLoaded) continue

			// will remove any invalid connections
			node.connections = getConnectionsFrom(pos, world)

			// will create nodes for any new ones
			for (con in node.connections) {
				if (nodes[world]!![con] == null) {
					nodes[world]!![con] = Node(con)
				}
			}
		}
	}

	@EventHandler
	fun onBlockPlaced(event: BlockPlaceEvent) {
		val pos = event.block.toBlockPos()
		val world = event.block.world
		// it will be automatically connected later
		if (world.getBlockState(pos.x, pos.y, pos.z).type == Material.COPPER_BLOCK)
			nodes.getOrPut(world) { mutableMapOf() }[pos] = Node(pos)
	}

	// turns out instantiating this every time getConnectionsFrom was called is slow, so here we are.
	private val offsets = arrayOf(
		Vec3i(0, 0, 1),
		Vec3i(0, 0, -1),
		Vec3i(0, 1, 0),
		Vec3i(0, -1, 0),
		Vec3i(1, 0, 0),
		Vec3i(-1, 0, 0)
	)

	private fun getConnectionsFrom(pos: BlockPos, world: World): MutableSet<BlockPos> {
		val found = mutableSetOf<BlockPos>()
		for (rel in offsets) {
			for (dist in 1..maxConnectionLength) {
				val next = pos.offset(rel.multiply(dist))
				when (world.getBlockState(next.x, next.y, next.z).type) {
					Material.LIGHTNING_ROD -> continue
					Material.COPPER_BLOCK -> found.add(next)
					else -> {}
				}
				break
			}
		}
		return found
	}

	@Serializable
	private data class PersistentNodeData(
		val x: Int,
		val y: Int,
		val z: Int,
		val c: Int
	)

	@EventHandler
	fun onWorldLoad(event: WorldLoadEvent) {
		if (!nodes[event.world].isNullOrEmpty()) {
			klogger.warn { "Loading pipe data for ${event.world.name}, but there is already existing data!" }
		}
		nodes[event.world] = mutableMapOf()
		event.world.persistentDataContainer.get(
			StellaricaServer.namespacedKey("pipenetworks"),
			PersistentDataType.STRING
		)?.let { string ->
			val data: Array<PersistentNodeData> = Json.decodeFromString(string)
			for (n in data) {
				val pos = BlockPos(n.x, n.y, n.z)
				nodes[event.world]!![pos] = Node(pos, content = n.c)
			}
		}
	}

	fun savePipes() {
		for (world in nodes.keys) {
			if (nodes[world].isNullOrEmpty()) return
			world.persistentDataContainer.set(
				StellaricaServer.namespacedKey("pipenetworks"),
				PersistentDataType.STRING,
				Json.encodeToString(nodes[world]!!.map {
					PersistentNodeData(
						it.key.x,
						it.key.y,
						it.key.z,
						it.value.content
					)
				})
			)
		}
	}
}
