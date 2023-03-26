package net.stellarica.server.transfer.pipes

import net.stellarica.server.transfer.Connection
import net.stellarica.server.transfer.Node
import net.stellarica.server.transfer.pipes.node.PipeNode

class PipeConnection(override var from: Node<Fuel>, override var to: Node<Fuel>) : Connection<Fuel> {
	override fun isValid(): Boolean {

		return true
	}
}