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
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.event.world.WorldLoadEvent
import org.bukkit.event.world.WorldUnloadEvent
import org.bukkit.persistence.PersistentDataType
import kotlin.math.min

object PipeHandler : Listener {
	const val maxTransferRate = 50
	const val maxConnectionLength = 16
	const val nodeCapacity = 100

	val nodes = mutableMapOf<World, MutableMap<BlockPos, Node>>()

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
			// get demand from connected nodes
			var demanding = 0
			for (other in node.connections.mapNotNull { nodes[world]!![it] }) {
				if (other.content < node.content) {
					demanding++
				}
			}
			// if there's no demand, don't do anything
			if (demanding == 0) continue

			for (other in node.connections.mapNotNull { nodes[world]!![it] }) {
				if (other.content < node.content) {
					// this means that a node connected to many other nodes may not transfer optimally!
					val transfer = min(min(min((node.content / demanding), maxTransferRate), other.capacity), node.content - node.outputBuffer)
					other.inputBuffer += transfer
					node.outputBuffer += transfer
				}
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

		nodes[world] = nodes[world]!!.filter { isNode(it.key, world) }.toMutableMap()

		for ((pos, node) in nodes[world]!!.toMap()) {
			// todo: if (world.isChunkLoaded(pos))

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
		if (isNode(pos, world)) nodes.getOrPut(world){mutableMapOf()}[pos] =  Node(pos)
	}

	private fun getConnectionsFrom(pos: BlockPos, world: World): MutableSet<BlockPos> {
		val found = mutableSetOf<BlockPos>()
		for (rel in listOf(
			Vec3i(0, 0, 1),
			Vec3i(0, 0, -1),
			Vec3i(0, 1, 0),
			Vec3i(0, -1, 0),
			Vec3i(1, 0, 0),
			Vec3i(-1, 0, 0)
		)) {
			for (dist in 1..maxConnectionLength) {
				val next = pos.offset(rel.multiply(dist))
				if (isConnector(next, world)) continue
				if (isNode(next, world)) {
					found.add(next)
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
		val content: Int
	)

	@EventHandler
	fun onWorldLoad(event: WorldLoadEvent) {
		if (!nodes[event.world].isNullOrEmpty()) {
			klogger.warn { "Loading pipe data for ${event.world.name}, but there is already existing data!"}
		}
		nodes[event.world] = mutableMapOf()
		event.world.persistentDataContainer.get(
			StellaricaServer.namespacedKey("pipenetworks"),
			PersistentDataType.STRING
		)?.let { string ->
			val data: Array<PersistentNodeData> = Json.decodeFromString(string)
			for (n in data) {
				val pos = BlockPos(n.x, n.y, n.z)
				nodes[event.world]!![pos] = Node(pos, content=n.content)
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
				}).also { println(it) }
			)
		}
	}

	private fun isConnector(blockPos: BlockPos, world: World): Boolean {
		return world.getBlockAt(blockPos.x, blockPos.y, blockPos.z).type == Material.LIGHTNING_ROD
	}

	private fun isNode(blockPos: BlockPos, world: World): Boolean {
		return world.getBlockAt(blockPos.x, blockPos.y, blockPos.z).type == Material.WAXED_COPPER_BLOCK
	}
}