package net.stellarica.server.transfer.node

import net.minecraft.core.BlockPos
import net.stellarica.common.util.OriginRelative
import net.stellarica.server.transfer.pipe.PipeHandler

class Node(
	val pos: BlockPos,
	var connections: MutableSet<BlockPos> = mutableSetOf(),
	var content: Int = 0,
	var capacity: Int = PipeHandler.nodeCapacity,
	var inputBuffer: Int = 0,
	var outputBuffer: Int = 0
)