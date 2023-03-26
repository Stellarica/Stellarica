package net.stellarica.server.transfer.pipes.node

import net.stellarica.common.utils.OriginRelative
import net.stellarica.server.transfer.Node
import net.stellarica.server.transfer.pipes.Fuel

abstract class PipeNode(
	var pos: OriginRelative
) : Node<Fuel>()