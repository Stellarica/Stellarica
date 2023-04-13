package net.stellarica.server.transfer.node

import net.minecraft.core.BlockPos
import net.stellarica.common.util.OriginRelative

class Node(
	val pos: BlockPos,
	var connections: MutableSet<OriginRelative>,
	var content: Int,
	var capacity: Int,
	var inputBuffer: Int,
	var outputBuffer: Int
)