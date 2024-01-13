package net.stellarica.server.multiblock.matching

import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block
import net.stellarica.server.material.block.type.BlockType

@JvmInline
value class BlockTagMatcher(private val tag: TagKey<Block>) : BlockMatcher {
	override fun matches(block: BlockType): Boolean {
		return block.getVanillaBlockState().tags.anyMatch { it == tag }
	}
}