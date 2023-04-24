package net.stellarica.server.multiblock.type

import net.minecraft.tags.BlockTags
import net.minecraft.world.level.block.Blocks
import net.stellarica.server.StellaricaServer
import net.stellarica.server.material.type.block.BlockType
import net.stellarica.server.multiblock.MultiblockType

@Suppress("unused")
object ShieldMultiblocks : MultiblockDef() {
	val TINY_SHIELD = object : MultiblockType() {
		override val displayName = "Tiny Shield"
		override val id = StellaricaServer.identifier("tiny_shield")
		override val blocks = mapOf(
			pos(0, 0, 0) match Blocks.DIAMOND_BLOCK,
			pos(1, 0, 0) match BlockTags.IMPERMEABLE,
			pos(-1, 0, 0) match BlockTags.IMPERMEABLE,
			pos(0, 0, 1) match setOf(Blocks.IRON_BLOCK, Blocks.GOLD_BLOCK),
			pos(0, 0, -1) match BlockTags.IMPERMEABLE
		)
	}

	val SMALL_SHIELD = object : MultiblockType() {
		override val displayName = "Small Shield"
		override val id = StellaricaServer.identifier("small_shield")
		override val blocks = mapOf(
			pos(0, 0, 0) match Blocks.DIAMOND_BLOCK,
			pos(0, 0, 1) match setOf(Blocks.IRON_BLOCK, Blocks.GOLD_BLOCK),
			pos(0, 0, -1) match setOf(Blocks.IRON_BLOCK, Blocks.GOLD_BLOCK),
			pos(1, 0, -1) match BlockTags.IMPERMEABLE,
			pos(1, 0, 0) match BlockTags.IMPERMEABLE,
			pos(1, 0, 1) match BlockTags.IMPERMEABLE,
			pos(-1, 0, -1) match BlockTags.IMPERMEABLE,
			pos(-1, 0, 0) match BlockTags.IMPERMEABLE,
			pos(-1, 0, 1) match BlockTags.IMPERMEABLE
		)
	}
}