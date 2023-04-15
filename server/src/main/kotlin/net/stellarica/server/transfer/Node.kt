package net.stellarica.server.transfer

import net.minecraft.core.BlockPos

class Node(
	val pos: BlockPos,
	var connections: MutableSet<BlockPos> = mutableSetOf(),
	var content: Int = 0,
	var capacity: Int = PipeHandler.nodeCapacity,
	var inputBuffer: Int = 0,
	var outputBuffer: Int = 0
)