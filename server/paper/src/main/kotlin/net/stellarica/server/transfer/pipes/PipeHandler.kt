package net.stellarica.server.transfer.pipes

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.stellarica.common.utils.OriginRelative
import net.stellarica.common.utils.toVec3
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

	fun detectPipeNetwork(origin: BlockPos, world: ServerLevel): PipeNetwork? {
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

		fun toNetwork(world: ServerLevel): PipeNetwork {
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
		for (active in activeNetworks.values.flatten()) {
			validateActiveNetwork(active)
		}
	}

	private fun validateActiveNetwork(active: PipeNetwork) {
		// todo: deduplicate code lol
		val undirectedNodes = mutableSetOf<Pair<OriginRelative, OriginRelative>>()
		val inputs = mutableSetOf<OriginRelative>()
		val outputs = mutableSetOf<OriginRelative>()

		detectConnectedPairs(
			active,
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

		val allValidConnections = undirectedNodes.map { mutableSetOf(it.first, it.second) }.toSet()
		val existing = mutableSetOf<Set<OriginRelative>>()

		for ((loc, node) in active.nodes) {
			for (connection in node.connections) {
				existing.add(setOf(loc, connection))
			}
		}

		for (existingConnection in existing) {
			if (allValidConnections.contains(existingConnection)) continue

			// this connection is invalid
			println("invalid connection $existingConnection")

			// remove the connection
			// if some other invalid connection caused the node to go poof, now is the time to check
			val n1 = active.nodes[existingConnection.first()]
			val n2 = active.nodes[existingConnection.last()]
			if (n1 == null || n2 == null) {
				println("invalid node in connection $existingConnection")
				continue
			}

			n1.connections.remove(existingConnection.last())
			n2.connections.remove(existingConnection.first())

			// determine which side of this break is larger
			// note that it still might be the same network if there was a loop
			fun recurse(pos: OriginRelative, checked: MutableSet<OriginRelative>) {
				checked.add(pos)
				for (connection in active.nodes[pos]?.connections ?: setOf()) {
					if (!checked.contains(connection)) {
						recurse(connection, checked)
					}
				}
			}

			// determine the nodes contained in each side of the break
			val checked1 = mutableSetOf<OriginRelative>()
			recurse(existingConnection.first(), checked1)
			val checked2 = mutableSetOf<OriginRelative>()
			recurse(existingConnection.last(), checked2)

			if (checked1 == checked2) {
				// there was a loop, it's still the same network, we don't need to do anything
				continue
			}

			// otherwise, we need to split the network
			// figure out which side contains the current origin, that will stay as this one
			val current = if (checked1.contains(OriginRelative(0, 0, 0))) checked1 else checked2
			val other = if (checked1.contains(OriginRelative(0, 0, 0))) checked2 else checked1


			val newOrigin =
				(if (current.contains(existingConnection.last())) existingConnection.first() else existingConnection.last()).getBlockPos(
					active.origin,
					active.direction
				)
			val offset = newOrigin.immutable().subtract(active.origin)

			val newNet = PipeNetwork(newOrigin, active.world, Direction.NORTH)
			activeNetworks[active.world.bukkit]!!.add(newNet)

			// move the disconnected nodes to the new network
			newNet.nodes.putAll(active.nodes.filter { it.key in other })
			// adjust the origin relative coordinates of the other one to reflect its new origin
			newNet.nodes =
				newNet.nodes.mapKeys { it.key.plus(OriginRelative(offset.x, offset.y, offset.z)) }.toMutableMap()
			for (newNode in newNet.nodes.values) {
				newNode.connections =
					newNode.connections.map { it.plus(OriginRelative(offset.x, offset.y, offset.z)) }.toMutableSet()
			}

			// remove the new network from the current one
			active.nodes = active.nodes.filter { it.key in other }.toMutableMap()
		}

		for (new in allValidConnections - existing) {
			// we didn't previously have these connections, expand the network
			val node1 = active.nodes.getOrPut(new.first()) { createNode(new.first()) }
			val node2 = active.nodes.getOrPut(new.last()) { createNode(new.last()) }
			node1.connections.add(node2.pos)
			node2.connections.add(node1.pos)
		}
	}
}