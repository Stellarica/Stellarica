package net.stellarica.server.transfer.pipes.node

import net.stellarica.common.utils.OriginRelative
import net.stellarica.server.transfer.Connection
import net.stellarica.server.transfer.Node
import net.stellarica.server.transfer.pipes.Fuel

abstract class PipeNode(
	var pos: OriginRelative,
	override var contents: Fuel?,
	override val incomingConnections: MutableSet<Connection<Fuel>>,
	override val outgoingConnections: MutableSet<Connection<Fuel>>
) : Node<Fuel> {

}