package net.stellarica.server.transfer.pipes

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.stellarica.common.utils.OriginRelative
import net.stellarica.server.transfer.nodes.NormalPipeNode
import net.stellarica.server.transfer.nodes.PipeInputNode
import net.stellarica.server.transfer.nodes.PipeNode
import net.stellarica.server.transfer.nodes.PipeOutputNode
import net.stellarica.server.utils.Tasks
import org.bukkit.event.Listener

object PipeHandler: Listener {
	init {
		Tasks.syncRepeat(10,10) {
			tickAllNetworks()
		}
		Tasks.syncRepeat(10, 20) {
			validateAllNetworks()
		}
	}

	const val maxTransfer = 50
	const val maxNodes = 100

	val networks = mutableSetOf<PipeNetwork>()

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
		if (net.nodes.size > maxNodes) return null

		networks.add(net)
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
				for (dist in 1..50) {
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

	private fun tickAllNetworks() {
		for (net in networks) {
			net.tick()
		}
	}

	private fun validateAllNetworks() {
		networks.removeIf { it.nodes.isEmpty() }
		// todo: ensure connections are valid
	}
}