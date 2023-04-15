package net.stellarica.server.transfer.pipe

import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.stellarica.server.transfer.node.Node
import net.stellarica.server.util.Tasks
import net.stellarica.server.util.extension.toBlockPos
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.world.WorldLoadEvent
import org.bukkit.event.world.WorldUnloadEvent
import kotlin.math.min

object PipeHandler : Listener {
	const val maxTransferRate = 50
	const val maxConnectionLength = 16
	const val nodeCapacity = 100

	val nodes = mutableMapOf<World, MutableMap<BlockPos, Node>>()

	init {
		Tasks.syncRepeat(20, 20) {
			for (world in nodes.keys) {
				tickPipes(world)
			}
		}
	}

	fun tickPipes(world: World) {
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

	@EventHandler
	fun onBlockPlaced(event: BlockPlaceEvent) {
		val pos = event.block.toBlockPos()
		val world = event.block.world
		if (isNode(pos, world) && nodes[world]?.containsKey(pos) != true) {
			val newNode = Node(pos)
			for (otherNode in getConnectionsFrom(pos, world)) {
				nodes.getOrPut(world) { mutableMapOf() }.getOrPut(otherNode) { Node(otherNode)}.connections.add(pos)
				newNode.connections.add(otherNode)
			}
		}
	}

	@EventHandler
	fun onBlockBroken(event: BlockBreakEvent) {
		val pos = event.block.toBlockPos()
		val world = event.block.world
		if (nodes[world]?.get(pos) != null) {
			for (connection in nodes[world]!!.remove(pos)!!.connections) {
				nodes[world]!![connection]!!.connections.remove(pos)
			}
		}
	}

	@EventHandler
	fun onWorldLoad(event: WorldLoadEvent) {

	}

	@EventHandler
	fun onWorldUnload(event: WorldUnloadEvent) {

	}

	fun getConnectionsFrom(pos: BlockPos, world: World): Set<BlockPos> {
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
		return found.toSet()
	}

	private fun isConnector(blockPos: BlockPos, world: World): Boolean {
		return world.getBlockAt(blockPos.x, blockPos.y, blockPos.z).type == Material.LIGHTNING_ROD
	}

	private fun isNode(blockPos: BlockPos, world: World): Boolean {
		return world.getBlockAt(blockPos.x, blockPos.y, blockPos.z).type == Material.WAXED_COPPER_BLOCK
	}
}