package net.stellarica.server.transfer.pipes

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.stellarica.common.utils.OriginRelative
import net.stellarica.server.StellaricaServer
import net.stellarica.server.transfer.nodes.Node
import net.stellarica.server.transfer.nodes.NormalPipeNode
import net.stellarica.server.transfer.nodes.PipeInputNode
import net.stellarica.server.transfer.nodes.PipeNode
import net.stellarica.server.transfer.nodes.PipeOutputNode
import net.stellarica.server.utils.Tasks
import net.stellarica.server.utils.extensions.bukkit
import net.stellarica.server.utils.extensions.vanilla
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.WorldLoadEvent
import org.bukkit.event.world.WorldUnloadEvent
import org.bukkit.persistence.PersistentDataType

object PipeHandler: Listener {
	init {
		Tasks.syncRepeat(10,10) {
			tickActiveNetworks()
		}
		Tasks.syncRepeat(10, 20) {
			activateNetworks()
		}
		Tasks.syncRepeat(10, 40) {
			validateActiveNetworks()
		}
	}

	const val maxTransferRate = 50
	const val maxNodes = 100
	const val maxConnectionLength = 50

	val activeNetworks = mutableMapOf<World, MutableSet<PipeNetwork>>()
	val inactiveNetworks = mutableMapOf<World, MutableSet<PipeNetwork>>()

	fun detectPipeNetwork(origin: BlockPos, world: ServerLevel): PipeNetwork? {
		val net = PipeNetwork(origin, world)

		val undirectedNodes = mutableSetOf<Pair<OriginRelative, OriginRelative>>()
		val inputs = mutableSetOf<OriginRelative>()
		val outputs = mutableSetOf<OriginRelative>()

		detectConnectedPairs(net, OriginRelative(0,0,0), undirectedNodes, mutableSetOf(OriginRelative(0,0,0)), inputs, outputs)

		fun createNode(pos: OriginRelative): PipeNode {
			return when (pos) {
				in inputs -> PipeInputNode(pos).also { it.content = 400 }
				in outputs -> PipeOutputNode(pos)
				else -> NormalPipeNode(pos)
			}
		}

		for ((p1, p2) in undirectedNodes) {
			net.nodes.getOrPut(p1) { createNode(p1) }.connections.add(p2)
			net.nodes.getOrPut(p2) { createNode(p2) }.connections.add(p1)
		}

		// todo: don't fail silently
		if (net.nodes.isEmpty() || net.nodes.size > maxNodes) return null

		inactiveNetworks.getOrPut(world.bukkit) { mutableSetOf() }.add(net)
		return net
	}

	private fun detectConnectedPairs(
		net: PipeNetwork,
		pos: OriginRelative,
		nodes: MutableSet<Pair<OriginRelative, OriginRelative>>,
		detected: MutableSet<OriginRelative>,
		inputs: MutableSet<OriginRelative>,
		outputs: MutableSet<OriginRelative>
	) {
		for (rel in listOf(
			OriginRelative(0,0,1),
			OriginRelative(0,0,-1),
			OriginRelative(0,1,0),
			OriginRelative(0,-1,0),
			OriginRelative(1,0,0),
			OriginRelative(-1,0,0)
		)) {
			if (net.isRod(pos + rel)) {
				for (dist in 1..maxConnectionLength) {
					val next = pos + rel * dist
					if (net.isRod(next)) continue
					if (net.isCopper(next)) {
						if (next !in detected) {
							if (net.isInput(next)) inputs.add(next)
							if (net.isOutput(next)) outputs.add(next)
							detected.add(next)
							detectConnectedPairs(net, next, nodes, detected, inputs, outputs)
						}
						if (!nodes.contains(pos to next) && !nodes.contains(next to pos)) nodes.add(pos to next)
					}
					break
				}
			}
		}
	}

	@Serializable
	data class PersistentNetworkData(
		val nodes: Set<Node>,
		val oX: Int,
		val oY: Int,
		val oZ: Int,
		val direction: Direction
	) {
		constructor(net: PipeNetwork): this(
			net.nodes.values.toSet(),
			net.origin.x,
			net.origin.y,
			net.origin.z,
			net.direction
		)

		fun toNetwork(world: ServerLevel): PipeNetwork {
			val net = PipeNetwork(BlockPos(oX, oY, oZ), world, direction)
			net.nodes.putAll(nodes.map { it.pos to it })
			return net
		}
	}


	@EventHandler
	fun onWorldLoad(event: WorldLoadEvent) {
		event.world.persistentDataContainer.get(StellaricaServer.namespacedKey("pipenetworks"), PersistentDataType.STRING)
			?.let { string ->
				Json.decodeFromString<Set<PersistentNetworkData>>(string)
					.map { it.toNetwork(event.world.vanilla) }
					.let { inactiveNetworks.getOrPut(event.world) { mutableSetOf() }.addAll(it) }
			}
	}

	@EventHandler
	fun onWorldUnload(event: WorldUnloadEvent) {

		val worldNetworks = activeNetworks.getOrDefault(event.world, setOf()).toMutableSet()
		worldNetworks.addAll(inactiveNetworks.getOrDefault(event.world, setOf()))

		event.world.persistentDataContainer.set(
			StellaricaServer.namespacedKey("pipenetworks"),
			PersistentDataType.STRING,
			Json.encodeToString(worldNetworks.map {
				PersistentNetworkData(it)
			})
		)
	}


	private fun tickActiveNetworks() {
		for (net in activeNetworks.values.flatten()) {
			net.tick()
		}
	}

	private fun activateNetworks() {
		// check that all active networks are in loaded chunks
		for (active in activeNetworks.values.flatten()) {
			if (!active.isInLoadedChunks()) {
				activeNetworks.getOrPut(active.world.bukkit) { mutableSetOf() }.remove(active)
				inactiveNetworks.getOrPut(active.world.bukkit) { mutableSetOf() }.add(active)
			}
		}

		// see if any inactive networks are in loaded chunks and should be active
		for (inactive in inactiveNetworks.values.flatten()) {
			if (inactive.isInLoadedChunks()) {
				inactiveNetworks.getOrPut(inactive.world.bukkit) { mutableSetOf() }.remove(inactive)
				activeNetworks.getOrPut(inactive.world.bukkit) { mutableSetOf() }.add(inactive)
			}
		}
	}

	private fun validateActiveNetworks() {
		// check that all connections in active networks are valid
		for (active in activeNetworks.values.flatten()) {
			// really jank solution: detect a new network and compare against it
			val temp = detectPipeNetwork(active.origin, active.world)!!
			inactiveNetworks[active.world.bukkit]!!.remove(temp)

			// check for removed or new connections
			for ((pos, node) in active.nodes) {
				val tempNode = temp.nodes[pos]
				if (tempNode?.connections != node.connections) {
					// either there is a new connection, or a connection broke
					val newConnections = tempNode!!.connections - node.connections
					val removedConnections = node.connections - tempNode.connections

					for (removed in removedConnections) {
						// maybe it is now a new separate network
						val newNet = detectPipeNetwork(removed.getBlockPos(active.origin, active.direction), active.world) ?: continue

						// any fuel that was in that part of our network should be transfered to the new network
						for ((newPos, newNode) in newNet.nodes) {
							newNode.content = active.nodes[newPos]?.content ?: 0
							active.nodes[newPos]?.content = 0 // just to be sure...
						}
					}

					node.connections.addAll(newConnections)
					node.connections.removeAll(removedConnections)
				}
			}


			// remove gone nodes
			active.nodes.keys.toSet().forEach {
				if (temp.nodes[it] == null) active.nodes.remove(it)
			}

			// add new nodes
			active.nodes.putAll(temp.nodes.filter { active.nodes[it.key] == null })

			// remove isolated nodes
			active.nodes.values.removeIf { it.connections.isEmpty() }

			if (active.nodes.isEmpty()) {
				activeNetworks[active.world.bukkit]!!.remove(active)
			}
		}
	}
}