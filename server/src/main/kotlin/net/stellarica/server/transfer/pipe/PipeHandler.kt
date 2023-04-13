package net.stellarica.server.transfer.pipe

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.stellarica.common.util.OriginRelative
import net.stellarica.server.StellaricaServer
import net.stellarica.server.transfer.node.Node
import net.stellarica.server.transfer.node.NormalPipeNode
import net.stellarica.server.transfer.node.PipeInputNode
import net.stellarica.server.transfer.node.PipeNode
import net.stellarica.server.transfer.node.PipeOutputNode
import net.stellarica.server.util.Tasks
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.WorldLoadEvent
import org.bukkit.event.world.WorldUnloadEvent
import org.bukkit.persistence.PersistentDataType

object PipeHandler : Listener {
	init {
		Tasks.syncRepeat(10, 10) {
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

	fun detectPipeNetwork(origin: BlockPos, world: World): PipeNetwork? {
		val net = PipeNetwork(origin, world)

		val undirectedNodes = mutableSetOf<Pair<OriginRelative, OriginRelative>>()
		val inputs = mutableSetOf<OriginRelative>()
		val outputs = mutableSetOf<OriginRelative>()

		detectConnectedPairs(
			net,
			OriginRelative(0, 0, 0),
			undirectedNodes,
			mutableSetOf(OriginRelative(0, 0, 0)),
			inputs,
			outputs
		)

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

		// todo: merge with any existing networks

		// todo: don't fail silently
		if (net.nodes.isEmpty() || net.nodes.size > maxNodes) return null

		inactiveNetworks.getOrPut(world) { mutableSetOf() }.add(net)
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
			OriginRelative(0, 0, 1),
			OriginRelative(0, 0, -1),
			OriginRelative(0, 1, 0),
			OriginRelative(0, -1, 0),
			OriginRelative(1, 0, 0),
			OriginRelative(-1, 0, 0)
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
		constructor(net: PipeNetwork) : this(
			net.nodes.values.toSet(),
			net.origin.x,
			net.origin.y,
			net.origin.z,
			net.direction
		)

		fun toNetwork(world: World): PipeNetwork {
			val net = PipeNetwork(BlockPos(oX, oY, oZ), world, direction)
			net.nodes.putAll(nodes.map { it.pos to it })
			return net
		}
	}


	@EventHandler
	fun onWorldLoad(event: WorldLoadEvent) {
		event.world.persistentDataContainer.get(
			StellaricaServer.namespacedKey("pipenetworks"),
			PersistentDataType.STRING
		)
			?.let { string ->
				Json.decodeFromString<Set<PersistentNetworkData>>(string)
					.map { it.toNetwork(event.world) }
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
				activeNetworks.getOrPut(active.world) { mutableSetOf() }.remove(active)
				inactiveNetworks.getOrPut(active.world) { mutableSetOf() }.add(active)
			}
		}

		// see if any inactive networks are in loaded chunks and should be active
		for (inactive in inactiveNetworks.values.flatten()) {
			if (inactive.isInLoadedChunks()) {
				inactiveNetworks.getOrPut(inactive.world) { mutableSetOf() }.remove(inactive)
				activeNetworks.getOrPut(inactive.world) { mutableSetOf() }.add(inactive)
			}
		}
	}

	private fun BlockPos.isPartOfNetwork(world: World): Boolean {
		var found = false
		for (net in (activeNetworks[world]!! + inactiveNetworks[world]!!)) {
			if (net.contains(this)) found = true
		}
		return found
	}

	private fun getPipeNetwork(pos: BlockPos, world: World): PipeNetwork? {
		for (net in (activeNetworks[world]!! + inactiveNetworks[world]!!)) {
			if (net.contains(pos)) return net
		}
		return null
	}

	private fun validateActiveNetworks() {
		for (net in activeNetworks.values.flatten()) {
			doStuffToNetwork(net)
		}
	}


	fun doStuffToNetwork(network: PipeNetwork) {
		activeNetworks[network.world]!!.remove(network)

		// get all valid connections
		// todo: deduplicate code lol
		val undirectedNodes = mutableSetOf<Pair<OriginRelative, OriginRelative>>()
		val inputs = mutableSetOf<OriginRelative>()
		val outputs = mutableSetOf<OriginRelative>()

		detectConnectedPairs(
			network,
			OriginRelative(0, 0, 0),
			undirectedNodes,
			mutableSetOf(OriginRelative(0, 0, 0)),
			inputs,
			outputs
		)

		val allConnections = undirectedNodes.map { mutableSetOf(it.first, it.second) }.toSet()
		val existingConnections = mutableSetOf<Set<OriginRelative>>()

		for ((loc, node) in network.nodes) {
			for (connection in node.connections) {
				existingConnections.add(setOf(loc, connection))
			}
		}

		val newValidConnections = allConnections.filter { it !in existingConnections }
		val newInvalidConnections = existingConnections.filter { it !in allConnections }

		// there are now a few cases:
		// - nothing changed, add it back to activeNetworks and we're done here
		// - a connection was added
		//	Either
		// 		- we can add it to this network if it isn't in one
		// 		- or we need to merge networks
		// - a connection was removed
		// Either
		//		- we can remove it from this network, and it's no longer part of one
		// 		- we need to split the network

		if (newValidConnections.isEmpty() && newInvalidConnections.isEmpty()) {
			// do nothing, nothing changed
			activeNetworks[network.world]!!.add(network)
			return
		}

		val networks = mutableSetOf(network)
		// split any broken connections first


		// and then merge
	}

	/** Whether this network is valid */
	private fun isValidNetwork(active: PipeNetwork): Boolean {
		return active.nodes.isNotEmpty()
	}

	private fun mergeNetworks(vararg nets: PipeNetwork): PipeNetwork {
		// in theory you can just add one network to another, but that was causing issues

		val world = nets.first().world
		nets.forEach { if (it.world != world) throw IllegalStateException() }

		data class AbsoluteNode(val pos: BlockPos, val content: Int, val connections: Set<BlockPos>)

		val nodes = mutableListOf<AbsoluteNode>()

		for (net in nets) {
			for (node in net.nodes) {
				nodes.add(AbsoluteNode(
					node.key.getBlockPos(net.origin, net.direction),
					node.value.content,
					node.value.connections.map { it.getBlockPos(net.origin, net.direction) }.toSet()
				))
			}
		}

		val origin = nets.first().origin

		val newNodes = mutableMapOf<OriginRelative, Node>()
		for (node in nodes) {
			val pos = OriginRelative.getOriginRelative(node.pos, origin, Direction.NORTH)
			newNodes[pos] = (NormalPipeNode( // todo: fix
				pos
			).also {
				it.connections = node.connections.map { OriginRelative.getOriginRelative(it, origin, Direction.NORTH) }.toMutableSet()
				it.content = node.content
			})
		}


		return PipeNetwork(
			origin,
			world,
			Direction.NORTH,
			newNodes
		)
	}

	private fun splitNetwork(network: PipeNetwork, p1: BlockPos, p2: BlockPos): Set<PipeNetwork> {
		val o1 = OriginRelative.getOriginRelative(p1, network.origin, network.direction)
		val o2 = OriginRelative.getOriginRelative(p1, network.origin, network.direction)

		// determine the nodes of both sides of the break
		// note that it still might be the same network if there was a loop
		fun recurse(pos: OriginRelative, checked: MutableSet<OriginRelative>) {
			checked.add(pos)
			for (connection in network.nodes[pos]?.connections ?: setOf()) {
				if (!checked.contains(connection)) {
					recurse(connection, checked)
				}
			}
		}
		val checked1 = mutableSetOf<OriginRelative>()
		recurse(o1, checked1)
		val checked2 = mutableSetOf<OriginRelative>()
		recurse(o2, checked2)

		if (checked1 == checked2) {
			// there was a loop, it's still the same network, we don't need to do anything
			println("Still the same network, no need to split.")
			return setOf(network)
		}

		println("Splitting network")

		data class AbsoluteNode(val content: Int, val connections: Set<BlockPos>)

		val nodes = mutableMapOf<BlockPos, AbsoluteNode>()
		for (node in network.nodes) {
			nodes[node.key.getBlockPos(network.origin, network.direction)] = (AbsoluteNode(
				node.value.content,
				node.value.connections.map { it.getBlockPos(network.origin, network.direction) }.toSet()
			))
		}

		val nets = mutableSetOf<PipeNetwork>()
		for (n in arrayOf(checked1, checked2)) {
			val g = n.map { it.getBlockPos(network.origin, network.direction)}
			val newNetwork = PipeNetwork(g.first(), network.world, Direction.NORTH)
			for (node in g) {
				val pos = OriginRelative.getOriginRelative(node, newNetwork.origin, newNetwork.direction)
				newNetwork.nodes[pos] = NormalPipeNode( // todo: fix
					pos
				).also {
					it.content = nodes[node]!!.content
					it.connections = nodes[node]!!.connections.map { OriginRelative.getOriginRelative(it, newNetwork.origin, newNetwork.direction)}.toMutableSet()
				}
			}
			nets.add(newNetwork)
		}
		return nets.toSet()
	}
}