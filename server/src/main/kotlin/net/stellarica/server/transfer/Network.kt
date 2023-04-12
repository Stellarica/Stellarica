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
			var demanding = 0
			for (other in node.connections.mapNotNull { nodes[it] }) {
				if (other.content < node.content) {
					demanding++
				}
			}
			// if there's no demand, don't do anything
			if (demanding == 0) continue

			for (other in node.connections.mapNotNull { nodes[it] }) {
				if (other.content < node.content) {
					// this means that a node connected to many other nodes may not transfer optimally!
					val transfer = min(min(min((node.content / demanding), maxTransferRate), other.capacity), node.content - node.outputBuffer)
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