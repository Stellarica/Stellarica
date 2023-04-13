package net.stellarica.server.multiblock.matching

import net.stellarica.server.material.type.block.BlockType

interface BlockMatcher {
	fun matches(block: BlockType): Boolean
}