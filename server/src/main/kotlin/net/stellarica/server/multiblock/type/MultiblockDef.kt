package net.stellarica.server.multiblock.type

import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block
import net.stellarica.common.coord.RelativePosition
import net.stellarica.server.material.custom.block.CustomBlock
import net.stellarica.server.material.type.block.BlockType
import net.stellarica.server.multiblock.matching.BlockMatcher
import net.stellarica.server.multiblock.matching.BlockTagMatcher
import net.stellarica.server.multiblock.matching.MultiBlockMatcher
import net.stellarica.server.multiblock.matching.SingleBlockMatcher

sealed class MultiblockDef {
	protected infix fun RelativePosition.match(tag: TagKey<Block>): Pair<RelativePosition, BlockMatcher> {
		return this to BlockTagMatcher(tag)
	}

	protected infix fun RelativePosition.match(block: BlockType): Pair<RelativePosition, BlockMatcher> {
		return this to SingleBlockMatcher(block)
	}

	protected infix fun RelativePosition.match(block: Block): Pair<RelativePosition, BlockMatcher> {
		return this match BlockType.of(block)
	}

	protected infix fun RelativePosition.match(block: CustomBlock): Pair<RelativePosition, BlockMatcher> {
		return this match BlockType.of(block)
	}

	protected infix fun RelativePosition.match(blocks: Collection<Any>): Pair<RelativePosition, BlockMatcher> {
		// this is kind of dumb but I'm against spamming BlockType.of() in multiblock definitions
		return this to MultiBlockMatcher(blocks.map { block ->
			when (block) {
				is BlockType -> block
				is CustomBlock -> BlockType.of(block)
				is Block -> BlockType.of(block)
				else -> throw IllegalArgumentException()
			}
		}.toSet())
	}

	// could use a typealias, but then it would have to be public
	protected fun pos(x: Int, y: Int, z: Int) = RelativePosition(x, y, z)
}