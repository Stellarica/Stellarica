package net.stellarica.server.multiblock.type

import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block
import net.stellarica.common.util.OriginRelative
import net.stellarica.server.material.custom.block.CustomBlock
import net.stellarica.server.material.type.block.BlockType
import net.stellarica.server.multiblock.matching.BlockMatcher
import net.stellarica.server.multiblock.matching.BlockTagMatcher
import net.stellarica.server.multiblock.matching.MultiBlockMatcher
import net.stellarica.server.multiblock.matching.SingleBlockMatcher

sealed class MultiblockDef {
	protected infix fun OriginRelative.matchTag(tag: TagKey<Block>): Pair<OriginRelative, BlockMatcher> {
		return this to BlockTagMatcher(tag)
	}

	protected infix fun OriginRelative.match(block: BlockType): Pair<OriginRelative, BlockMatcher> {
		return this to SingleBlockMatcher(block)
	}

	protected infix fun OriginRelative.match(block: Block): Pair<OriginRelative, BlockMatcher> {
		return this match BlockType.of(block)
	}

	protected infix fun OriginRelative.match(block: CustomBlock): Pair<OriginRelative, BlockMatcher> {
		return this match BlockType.of(block)
	}

	protected fun OriginRelative.matchAny(vararg blocks: BlockType): Pair<OriginRelative, BlockMatcher> {
		return this to MultiBlockMatcher(blocks.toSet())
	}

	// could use a typealias, but then it would have to be public
	protected fun pos(x: Int, y: Int, z: Int) = OriginRelative(x, y, z)
}