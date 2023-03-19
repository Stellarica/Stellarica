package net.stellarica.server.multiblocks

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Blocks
import net.stellarica.common.utils.OriginRelative
import net.stellarica.server.StellaricaServer
import net.stellarica.server.StellaricaServer.Companion.identifier
import net.stellarica.server.material.custom.item.CustomItem
import net.stellarica.server.material.type.block.BlockType
import net.stellarica.server.material.type.item.ItemType

@Suppress("Unused") // iea
object Multiblocks {
	val TEST_LINEAR_WEAPON = MultiblockType(
		identifier("test_linear_weapon"),
		mapOf(
			OriginRelative(0, 0, 0) to BlockType.of(Blocks.CHISELED_STONE_BRICKS),
			OriginRelative(1, 0, 0) to BlockType.of(Blocks.IRON_BLOCK),
			OriginRelative(2, 0, 0) to BlockType.of(Blocks.COPPER_BLOCK),
			OriginRelative(3, 0, 0) to BlockType.of(Blocks.DEEPSLATE_TILE_WALL),
			OriginRelative(4, 0, 0) to BlockType.of(Blocks.COPPER_BLOCK),
			OriginRelative(5, 0, 0) to BlockType.of(Blocks.DEEPSLATE_TILE_WALL),
			OriginRelative(6, 0, 0) to BlockType.of(Blocks.DEEPSLATE_TILE_WALL),
			OriginRelative(7, 0, 0) to BlockType.of(Blocks.DEEPSLATE_TILE_WALL),
			OriginRelative(0, -1, 0) to BlockType.of(Blocks.IRON_BLOCK),
			OriginRelative(1, -1, 0) to BlockType.of(Blocks.DISPENSER),
			OriginRelative(2, -1, 0) to BlockType.of(Blocks.IRON_BLOCK)
			
			
		)
	)

	val TEST_INSTANT_WEAPON = MultiblockType(
		identifier("test_instant_weapon"),
		mapOf(
			OriginRelative(0, 0, 0) to BlockType.of(Blocks.CHISELED_STONE_BRICKS),
			OriginRelative(1, 0, 0) to BlockType.of(Blocks.IRON_BLOCK),
			OriginRelative(2, 0, 0) to BlockType.of(Blocks.REDSTONE_BLOCK),
			OriginRelative(4, 0, 0) to BlockType.of(Blocks.RED_STAINED_GLASS),
			OriginRelative(1, 0, 1) to BlockType.of(Blocks.DEEPSLATE_TILE_WALL),
			OriginRelative(2, 0, 1) to BlockType.of(Blocks.DEEPSLATE_TILE_WALL),
			OriginRelative(3, 0, 1) to BlockType.of(Blocks.DEEPSLATE_TILE_WALL),
			OriginRelative(1, 0, -1) to BlockType.of(Blocks.DEEPSLATE_TILE_WALL),
			OriginRelative(2, 0, -1) to BlockType.of(Blocks.DEEPSLATE_TILE_WALL),
			OriginRelative(3, 0, -1) to BlockType.of(Blocks.DEEPSLATE_TILE_WALL)
		)
	)

	val TEST_ACCELERATING_WEAPON = MultiblockType(
		identifier("test_accelerating_weapon"),
		mapOf(
			OriginRelative(0, 0, 0) to BlockType.of(Blocks.CHISELED_STONE_BRICKS),
			OriginRelative(1, 0, 0) to BlockType.of(Blocks.GOLD_BLOCK),
			OriginRelative(2, 0, 0) to BlockType.of(Blocks.WHITE_STAINED_GLASS),
			OriginRelative(3, 0, 0) to BlockType.of(Blocks.DIAMOND_BLOCK),
			OriginRelative(4, 0, 0) to BlockType.of(Blocks.WHITE_STAINED_GLASS),
			OriginRelative(5, 0, 0) to BlockType.of(Blocks.GOLD_BLOCK),
			OriginRelative(6, 0, 0) to BlockType.of(Blocks.WHITE_STAINED_GLASS),
			OriginRelative(7, 0, 0) to BlockType.of(Blocks.DISPENSER),
			OriginRelative(0, 0, 1) to BlockType.of(Blocks.IRON_BLOCK),
			OriginRelative(1, 0, 1) to BlockType.of(Blocks.IRON_BLOCK),
			OriginRelative(2, 0, 1) to BlockType.of(Blocks.DEEPSLATE_TILE_WALL),
			OriginRelative(3, 0, 1) to BlockType.of(Blocks.IRON_BLOCK),
			OriginRelative(4, 0, 1) to BlockType.of(Blocks.DEEPSLATE_TILE_WALL),
			OriginRelative(5, 0, 1) to BlockType.of(Blocks.IRON_BLOCK),
			OriginRelative(6, 0, 1) to BlockType.of(Blocks.DEEPSLATE_TILE_WALL),
			OriginRelative(7, 0, 1) to BlockType.of(Blocks.IRON_BLOCK),
			OriginRelative(0, 0, -1) to BlockType.of(Blocks.IRON_BLOCK),
			OriginRelative(1, 0, -1) to BlockType.of(Blocks.IRON_BLOCK),
			OriginRelative(2, 0, -1) to BlockType.of(Blocks.DEEPSLATE_TILE_WALL),
			OriginRelative(3, 0, -1) to BlockType.of(Blocks.IRON_BLOCK),
			OriginRelative(4, 0, -1) to BlockType.of(Blocks.DEEPSLATE_TILE_WALL),
			OriginRelative(5, 0, -1) to BlockType.of(Blocks.IRON_BLOCK),
			OriginRelative(6, 0, -1) to BlockType.of(Blocks.DEEPSLATE_TILE_WALL),
			OriginRelative(7, 0, -1) to BlockType.of(Blocks.IRON_BLOCK)
		)
	)

	val TEST_SHIELD = MultiblockType(
		identifier("test_shield"),
		mapOf(
			OriginRelative(0, 0, 0) to BlockType.of(Blocks.DIAMOND_BLOCK),
			OriginRelative(1, 0, 0) to BlockType.of(Blocks.GLASS),
			OriginRelative(-1, 0, 0) to BlockType.of(Blocks.GLASS),
			OriginRelative(0, 0, 1) to BlockType.of(Blocks.GLASS),
			OriginRelative(0, 0, -1) to BlockType.of(Blocks.GLASS),
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
}
