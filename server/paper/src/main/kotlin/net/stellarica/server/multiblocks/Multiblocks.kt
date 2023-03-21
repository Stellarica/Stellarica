package net.stellarica.server.multiblocks

import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.stellarica.common.utils.OriginRelative
import net.stellarica.server.StellaricaServer
import net.stellarica.server.StellaricaServer.Companion.identifier
import net.stellarica.server.material.custom.block.CustomBlock
import net.stellarica.server.material.custom.item.CustomItem
import net.stellarica.server.material.type.block.BlockType
import net.stellarica.server.material.type.block.CustomBlockType
import net.stellarica.server.material.type.item.ItemType
import net.stellarica.server.multiblocks.matching.BlockMatcher
import net.stellarica.server.multiblocks.matching.BlockTagMatcher
import net.stellarica.server.multiblocks.matching.MultiBlockMatcher
import net.stellarica.server.multiblocks.matching.SingleBlockMatcher
import org.bukkit.Material
import org.bukkit.Tag

@Suppress("Unused") // iea
object Multiblocks {
	val LIGHT_RAILGUN = MultiblockType(
		identifier("light_railgun"),
		mapOf(
			Pos(0, 0, 0) match Blocks.CHISELED_STONE_BRICKS,
			Pos(1, 0, 0) match Blocks.IRON_BLOCK,
			Pos(2, 0, 0) match Blocks.COPPER_BLOCK,
			Pos(3, 0, 0) matchTag BlockTags.WALLS,
			Pos(4, 0, 0) match Blocks.COPPER_BLOCK,
			Pos(5, 0, 0) matchTag BlockTags.WALLS,
			Pos(6, 0, 0) matchTag BlockTags.WALLS,
			Pos(7, 0, 0) matchTag BlockTags.WALLS,
			Pos(0, -1, 0) match Blocks.IRON_BLOCK,
			Pos(1, -1, 0) match Blocks.DISPENSER,
			Pos(2, -1, 0) match Blocks.IRON_BLOCK
			
			
		)
	)

	val PULSE_LASER = MultiblockType(
		identifier("pulse_laser"),
		mapOf(
			Pos(0, 0, 0) match Blocks.CHISELED_STONE_BRICKS,
			Pos(1, 0, 0) match Blocks.IRON_BLOCK,
			Pos(2, 0, 0) match Blocks.REDSTONE_BLOCK,
			Pos(4, 0, 0) matchTag BlockTags.IMPERMEABLE,
			Pos(1, 0, 1) matchTag BlockTags.WALLS,
			Pos(2, 0, 1) matchTag BlockTags.WALLS,
			Pos(3, 0, 1) matchTag BlockTags.WALLS,
			Pos(1, 0, -1) matchTag BlockTags.WALLS,
			Pos(2, 0, -1) matchTag BlockTags.WALLS,
			Pos(3, 0, -1) matchTag BlockTags.WALLS
		)
	)

	val PLASMA_CANNON = MultiblockType(
		identifier("plasma_cannon"),
		mapOf(
			Pos(0, 0, 0) match Blocks.CHISELED_STONE_BRICKS,
			Pos(1, 0, 0) match Blocks.GOLD_BLOCK,
			Pos(2, 0, 0) matchTag BlockTags.IMPERMEABLE,
			Pos(3, 0, 0) match Blocks.DIAMOND_BLOCK,
			Pos(4, 0, 0) matchTag BlockTags.IMPERMEABLE,
			Pos(5, 0, 0) match Blocks.GOLD_BLOCK,
			Pos(6, 0, 0) matchTag BlockTags.IMPERMEABLE,
			Pos(7, 0, 0) match Blocks.DISPENSER,
			Pos(0, 0, 1) match Blocks.IRON_BLOCK,
			Pos(1, 0, 1) match Blocks.IRON_BLOCK,
			Pos(2, 0, 1) matchTag BlockTags.WALLS,
			Pos(3, 0, 1) match Blocks.IRON_BLOCK,
			Pos(4, 0, 1) matchTag BlockTags.WALLS,
			Pos(5, 0, 1) match Blocks.IRON_BLOCK,
			Pos(6, 0, 1) matchTag BlockTags.WALLS,
			Pos(7, 0, 1) match Blocks.IRON_BLOCK,
			Pos(0, 0, -1) match Blocks.IRON_BLOCK,
			Pos(1, 0, -1) match Blocks.IRON_BLOCK,
			Pos(2, 0, -1) matchTag BlockTags.WALLS,
			Pos(3, 0, -1) match Blocks.IRON_BLOCK,
			Pos(4, 0, -1) matchTag BlockTags.WALLS,
			Pos(5, 0, -1) match Blocks.IRON_BLOCK,
			Pos(6, 0, -1) matchTag BlockTags.WALLS,
			Pos(7, 0, -1) match Blocks.IRON_BLOCK
		)
	)

	val TINY_SHIELD = MultiblockType(
		identifier("tiny_shield"),
		mapOf(
			Pos(0, 0, 0) match Blocks.DIAMOND_BLOCK,
			Pos(1, 0, 0) matchTag BlockTags.IMPERMEABLE,
			Pos(-1, 0, 0) matchTag BlockTags.IMPERMEABLE,
			Pos(0, 0, 1).matchAny(BlockType.of(Blocks.IRON_BLOCK), BlockType.of(Blocks.GOLD_BLOCK)),
			Pos(-1, 0, 0) matchTag BlockTags.IMPERMEABLE
		)
	)

	val SMALL_SHIELD = MultiblockType(
		identifier("small_shield"),
		mapOf(
			Pos(0, 0, 0) match Blocks.DIAMOND_BLOCK,
			Pos(0, 0, 1).matchAny(BlockType.of(Blocks.IRON_BLOCK), BlockType.of(Blocks.GOLD_BLOCK)),
			Pos(0, 0, -1).matchAny(BlockType.of(Blocks.IRON_BLOCK), BlockType.of(Blocks.GOLD_BLOCK)),
			Pos(1, 0, -1) matchTag BlockTags.IMPERMEABLE,
			Pos(1, 0, 0) matchTag BlockTags.IMPERMEABLE,
			Pos(1, 0, 1) matchTag BlockTags.IMPERMEABLE,
			Pos(-1, 0, -1) matchTag BlockTags.IMPERMEABLE,
			Pos(-1, 0, 0) matchTag BlockTags.IMPERMEABLE,
			Pos(-1, 0, 1) matchTag BlockTags.IMPERMEABLE
		)
	)

	fun all(): Set<MultiblockType> { // can't do lazy{} because reflection
		return this::class.java.declaredFields.mapNotNull { it.get(this) as? MultiblockType }.toSet()
	}

	fun byId(id: ResourceLocation): MultiblockType? {
		// todo: this could probably be better optimized.
		// maybe keep around a hashmap?
		return all().firstOrNull { it.id == id }
	}


	private infix fun OriginRelative.matchTag(tag: TagKey<Block>): Pair<OriginRelative, BlockMatcher> {
		return this to BlockTagMatcher(tag)
	}

	private infix fun OriginRelative.match(block: BlockType): Pair<OriginRelative, BlockMatcher> {
		return this to SingleBlockMatcher(block)
	}

	private infix fun OriginRelative.match(block: Block): Pair<OriginRelative, BlockMatcher> {
		return this match BlockType.of(block)
	}

	private infix fun OriginRelative.match(block: CustomBlock): Pair<OriginRelative, BlockMatcher> {
		return this match BlockType.of(block)
	}

	private fun OriginRelative.matchAny(vararg blocks: BlockType): Pair<OriginRelative, BlockMatcher> {
		return this to MultiBlockMatcher(blocks.toSet())
	}
}

private typealias Pos = OriginRelative
