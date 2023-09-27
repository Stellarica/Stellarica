package net.stellarica.server.multiblock.matching

import net.stellarica.server.material.type.block.BlockType

@JvmInline
value class SingleBlockMatcher(private val block: BlockType) : BlockMatcher {
	override fun matches(block: BlockType): Boolean = block == this.block
}