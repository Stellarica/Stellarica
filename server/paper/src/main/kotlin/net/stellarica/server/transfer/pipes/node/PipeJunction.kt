package net.stellarica.server.transfer.pipes.node

import net.stellarica.common.utils.OriginRelative
import net.stellarica.server.transfer.Node
import net.stellarica.server.transfer.pipes.Fuel

class PipeJunction(pos: OriginRelative) : PipeNode(pos) {
	override fun validateOutgoingConnections() {
		TODO("Not yet implemented")
	}

	override fun transfer(): Set<Node<Fuel>> {
		TODO("Not yet implemented")
	}
}