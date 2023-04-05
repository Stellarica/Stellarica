package net.stellarica.server.transfer

import net.stellarica.common.utils.OriginRelative
import net.stellarica.server.transfer.nodes.Node
import kotlin.math.min

abstract class Network {
	abstract val maxTransferRate: Int
	abstract val nodes: MutableMap<OriginRelative, Node>

	fun tick() {
		for (node in nodes.values) {
			// get demand from connected nodes
			var demand = 0
			for (other in node.connections.mapNotNull { nodes[it] }) {
				if (other.content < node.content) {
					demand += min(node.content - other.content, maxTransferRate)
				}
			}
			// if there's no demand, don't do anything
			if (demand <= 0) continue

			// if we can't supply all demand, supply a fraction of it
			// if we have enough fuel to satisfy all demand, this will be 1
			// kinda jank but it seems to work
			var num = 1f
			while (demand / num > node.content) {
				num++
			}

			for (other in node.connections.mapNotNull { nodes[it] }) {
				if (other.content < node.content) {
					val transfer = min(((node.content - other.content) / num).toInt(), maxTransferRate)
					other.inputBuffer += transfer
					node.outputBuffer += transfer
				}
			}
		}

		// apply changes at once
		for (node in nodes.values) {
			node.content += node.inputBuffer
			node.content -= node.outputBuffer
			node.inputBuffer = 0
			node.outputBuffer = 0
		}
	}
}