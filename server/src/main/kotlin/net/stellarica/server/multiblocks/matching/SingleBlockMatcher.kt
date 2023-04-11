package net.stellarica.server.multiblocks.matching

import net.stellarica.server.material.type.block.BlockType

@JvmInline
value class SingleBlockMatcher(val block: BlockType) : BlockMatcher {
	override fun matches(block: BlockType): Boolean = block == this.block
}