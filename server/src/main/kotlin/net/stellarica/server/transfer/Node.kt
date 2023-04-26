package net.stellarica.server.transfer

import net.minecraft.core.BlockPos

class Node(
	var pos: BlockPos,
	var connections: MutableSet<BlockPos> = mutableSetOf(),
	val content: MutableSet<Packet> = mutableSetOf(),
	val inputBuffer: MutableSet<Packet> = mutableSetOf()
)