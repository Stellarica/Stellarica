package net.stellarica.server.multiblock.type

import net.minecraft.tags.BlockTags
import net.minecraft.world.level.block.Blocks
import net.stellarica.server.StellaricaServer
import net.stellarica.server.material.type.block.BlockType
import net.stellarica.server.multiblock.data.EmptyMultiblockData
import net.stellarica.server.multiblock.MultiblockInstance
import net.stellarica.server.multiblock.MultiblockType
import net.stellarica.server.multiblock.data.PowerableMultiblockData

@Suppress("unused")
object ShieldMultiblocks : MultiblockDef() {
	val TINY_SHIELD = object : MultiblockType {
		override val displayName = "Tiny Shield"
		override val id = StellaricaServer.identifier("tiny_shield")
		override val blocks = mapOf(
			pos(0, 0, 0) match Blocks.DIAMOND_BLOCK,
			pos(1, 0, 0) matchTag BlockTags.IMPERMEABLE,
			pos(-1, 0, 0) matchTag BlockTags.IMPERMEABLE,
			pos(0, 0, 1).matchAny(BlockType.of(Blocks.IRON_BLOCK), BlockType.of(Blocks.GOLD_BLOCK)),
			pos(0, 0, -1) matchTag BlockTags.IMPERMEABLE
		)

		override val dataType = PowerableMultiblockData()

		override fun tick(instance: MultiblockInstance) {
			val data = (instance.data as PowerableMultiblockData)
			data.power += 1
		}
	}

	val SMALL_SHIELD = object : MultiblockType {
		override val displayName = "Small Shield"
		override val id = StellaricaServer.identifier("small_shield")
		override val blocks = mapOf(
			pos(0, 0, 0) match Blocks.DIAMOND_BLOCK,
			pos(0, 0, 1).matchAny(BlockType.of(Blocks.IRON_BLOCK), BlockType.of(Blocks.GOLD_BLOCK)),
			pos(0, 0, -1).matchAny(BlockType.of(Blocks.IRON_BLOCK), BlockType.of(Blocks.GOLD_BLOCK)),
			pos(1, 0, -1) matchTag BlockTags.IMPERMEABLE,
			pos(1, 0, 0) matchTag BlockTags.IMPERMEABLE,
			pos(1, 0, 1) matchTag BlockTags.IMPERMEABLE,
			pos(-1, 0, -1) matchTag BlockTags.IMPERMEABLE,
			pos(-1, 0, 0) matchTag BlockTags.IMPERMEABLE,
			pos(-1, 0, 1) matchTag BlockTags.IMPERMEABLE
		)


		override val dataType = EmptyMultiblockData()

		override fun tick(instance: MultiblockInstance) {

		}
	}
}