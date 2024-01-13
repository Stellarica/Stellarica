package net.stellarica.server.multiblock.matching

import net.stellarica.server.material.block.type.BlockType

interface BlockMatcher {
	fun matches(block: BlockType): Boolean
}