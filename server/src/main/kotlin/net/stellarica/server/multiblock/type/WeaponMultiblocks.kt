package net.stellarica.server.multiblock.type

import net.minecraft.tags.BlockTags
import net.minecraft.world.level.block.Blocks
import net.stellarica.server.StellaricaServer
import net.stellarica.server.multiblock.MultiblockType

@Suppress("unused")
object WeaponMultiblocks : MultiblockDef() {
	val LIGHT_RAILGUN = object : MultiblockType() {
		override val displayName = "Light Railgun"
		override val id = StellaricaServer.identifier("light_railgun")
		override val blocks = mapOf(
				pos(0, 0, 0) match Blocks.CHISELED_STONE_BRICKS,
				pos(1, 0, 0) match Blocks.IRON_BLOCK,
				pos(2, 0, 0) match Blocks.COPPER_BLOCK,
				pos(3, 0, 0) match BlockTags.WALLS,
				pos(4, 0, 0) match Blocks.COPPER_BLOCK,
				pos(5, 0, 0) match BlockTags.WALLS,
				pos(6, 0, 0) match BlockTags.WALLS,
				pos(7, 0, 0) match BlockTags.WALLS,
				pos(0, -1, 0) match Blocks.IRON_BLOCK,
				pos(1, -1, 0) match Blocks.DISPENSER,
				pos(2, -1, 0) match Blocks.IRON_BLOCK

		)
	}

	val PULSE_LASER = object : MultiblockType() {
		override val displayName = "Pulse Laser"
		override val id = StellaricaServer.identifier("pulse_laser")
		override val blocks = mapOf(
				pos(0, 0, 0) match Blocks.CHISELED_STONE_BRICKS,
				pos(1, 0, 0) match Blocks.IRON_BLOCK,
				pos(2, 0, 0) match Blocks.REDSTONE_BLOCK,
				pos(4, 0, 0) match BlockTags.IMPERMEABLE,
				pos(1, 0, 1) match BlockTags.WALLS,
				pos(2, 0, 1) match BlockTags.WALLS,
				pos(3, 0, 1) match BlockTags.WALLS,
				pos(1, 0, -1) match BlockTags.WALLS,
				pos(2, 0, -1) match BlockTags.WALLS,
				pos(3, 0, -1) match BlockTags.WALLS
		)
	}

	val PLASMA_CANNON = object : MultiblockType() {
		override val displayName = "Plasma Cannon"
		override val id = StellaricaServer.identifier("plasma_cannon")
		override val blocks = mapOf(
				pos(0, 0, 0) match Blocks.CHISELED_STONE_BRICKS,
				pos(1, 0, 0) match Blocks.GOLD_BLOCK,
				pos(2, 0, 0) match BlockTags.IMPERMEABLE,
				pos(3, 0, 0) match Blocks.DIAMOND_BLOCK,
				pos(4, 0, 0) match BlockTags.IMPERMEABLE,
				pos(5, 0, 0) match Blocks.GOLD_BLOCK,
				pos(6, 0, 0) match BlockTags.IMPERMEABLE,
				pos(7, 0, 0) match Blocks.DISPENSER,
				pos(0, 0, 1) match Blocks.IRON_BLOCK,
				pos(1, 0, 1) match Blocks.IRON_BLOCK,
				pos(2, 0, 1) match BlockTags.WALLS,
				pos(3, 0, 1) match Blocks.IRON_BLOCK,
				pos(4, 0, 1) match BlockTags.WALLS,
				pos(5, 0, 1) match Blocks.IRON_BLOCK,
				pos(6, 0, 1) match BlockTags.WALLS,
				pos(7, 0, 1) match Blocks.IRON_BLOCK,
				pos(0, 0, -1) match Blocks.IRON_BLOCK,
				pos(1, 0, -1) match Blocks.IRON_BLOCK,
				pos(2, 0, -1) match BlockTags.WALLS,
				pos(3, 0, -1) match Blocks.IRON_BLOCK,
				pos(4, 0, -1) match BlockTags.WALLS,
				pos(5, 0, -1) match Blocks.IRON_BLOCK,
				pos(6, 0, -1) match BlockTags.WALLS,
				pos(7, 0, -1) match Blocks.IRON_BLOCK
		)
	}

	val BATTLE_CANNON = object : MultiblockType() {
		override val displayName = "Battle Cannon"
		override val id = StellaricaServer.identifier("battle_cannon")
		override val blocks = mapOf(
				pos(0, 0, 0) match Blocks.CHISELED_STONE_BRICKS,
				pos(1, 0, 0) match Blocks.IRON_BLOCK,
				pos(2, 0, 0) match Blocks.IRON_BLOCK,
				pos(3, 0, 0) match BlockTags.SLABS,
				pos(4, 0, 0) match BlockTags.SLABS,
				pos(5, 0, 0) match BlockTags.WALLS,
				pos(6, 0, 0) match BlockTags.WALLS,
				pos(7, 0, 0) match BlockTags.WALLS,
				pos(0, -1, 0) match BlockTags.SLABS,
				pos(1, -1, 0) match Blocks.DISPENSER,
				pos(2, -1, 0) match Blocks.IRON_BLOCK,
				pos(3, -1, 0) match BlockTags.SLABS
		)
	}
}