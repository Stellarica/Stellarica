package net.stellarica.server.multiblock.matching

import net.stellarica.server.material.block.type.BlockType

@JvmInline
value class MultiBlockMatcher(private val types: Collection<BlockType>) : BlockMatcher {
	override fun matches(block: BlockType) = block in types
}