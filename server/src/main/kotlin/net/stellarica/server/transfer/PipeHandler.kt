package net.stellarica.server.transfer

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.stellarica.server.StellaricaServer
import net.stellarica.server.StellaricaServer.Companion.klogger
import net.stellarica.server.util.BlockPosSerializer
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

object PipeHandler : Listener {
	const val maxConnectionLength = 32
	const val maxPacketCount = 4

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
			if (node.content.isEmpty()) continue

			val connections = node.connections.mapNotNull { nodes[world]!![it] }

			for (other in connections) {
				if (other.content.size >= maxPacketCount) continue
				for (packet in node.content.toTypedArray()) {
					if (packet.previousNode == other.pos) continue
					packet.previousNode = node.pos
					other.inputBuffer.add(packet)
					node.content.remove(packet)
				}
			}
		}

		// apply changes at once
		for ((_, node) in nodes[world] ?: mutableMapOf()) {
			node.content.addAll(node.inputBuffer)
			node.inputBuffer.clear()
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
		if (world.getBlockState(pos.x, pos.y, pos.z).type == Material.CUT_COPPER)
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
					Material.CUT_COPPER -> found.add(next)
					else -> {}
				}
				break
			}
		}
		return found
	}

	@Serializable
	private data class PersistentNodeData(
		@Serializable(with = BlockPosSerializer::class)
		val p: BlockPos,
		val c: MutableSet<Packet>
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
				nodes[event.world]!![n.p] = Node(n.p, content = n.c)
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
					PersistentNodeData(it.key, it.value.content)
				})
			)
		}
	}
}
