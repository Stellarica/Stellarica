package net.stellarica.server.multiblock

import net.minecraft.core.Vec3i
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.stellarica.common.util.OriginRelative
import net.stellarica.server.StellaricaServer.Companion.identifier
import net.stellarica.server.material.custom.block.CustomBlock
import net.stellarica.server.material.type.block.BlockType
import net.stellarica.server.multiblock.FuelableMultiblockData.Companion.transferFuelWith
import net.stellarica.server.multiblock.matching.BlockMatcher
import net.stellarica.server.multiblock.matching.BlockTagMatcher
import net.stellarica.server.multiblock.matching.MultiBlockMatcher
import net.stellarica.server.multiblock.matching.SingleBlockMatcher
import net.stellarica.server.transfer.PipeHandler

@Suppress("Unused") // iea
object Multiblocks {
	val LIGHT_RAILGUN = object : MultiblockType {
		override val displayName = "Light Railgun"
		override val id = identifier("light_railgun")
		override val blocks = mapOf(
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

		override val dataType = EmptyMultiblockData()

		override fun tick(instance: MultiblockInstance) {

		}
	}


	val PULSE_LASER = object : MultiblockType {
		override val displayName = "Pulse Laser"
		override val id = identifier("pulse_laser")
		override val blocks = mapOf(
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

		override val dataType = EmptyMultiblockData()

		override fun tick(instance: MultiblockInstance) {

		}
	}

	val PLASMA_CANNON = object : MultiblockType {
		override val displayName = "Plasma Cannon"
		override val id = identifier("plasma_cannon")
		override val blocks = mapOf(
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

		override val dataType = EmptyMultiblockData()

		override fun tick(instance: MultiblockInstance) {

		}
	}

	val BATTLE_CANNON = object : MultiblockType {
		override val displayName = "Battle Cannon"
		override val id = identifier("battle_cannon")
		override val blocks = mapOf(
			Pos(0, 0, 0) match Blocks.CHISELED_STONE_BRICKS,
			Pos(1, 0, 0) match Blocks.IRON_BLOCK,
			Pos(2, 0, 0) match Blocks.IRON_BLOCK,
			Pos(3, 0, 0) matchTag BlockTags.SLABS,
			Pos(4, 0, 0) matchTag BlockTags.SLABS,
			Pos(5, 0, 0) matchTag BlockTags.WALLS,
			Pos(6, 0, 0) matchTag BlockTags.WALLS,
			Pos(7, 0, 0) matchTag BlockTags.WALLS,
			Pos(0, -1, 0) matchTag BlockTags.SLABS,
			Pos(1, -1, 0) match Blocks.DISPENSER,
			Pos(2, -1, 0) match Blocks.IRON_BLOCK,
			Pos(3, -1, 0) matchTag BlockTags.SLABS
		)

		override val dataType = EmptyMultiblockData()

		override fun tick(instance: MultiblockInstance) {

		}
	}

	val TINY_SHIELD = object : MultiblockType {
		override val displayName = "Tiny Shield"
		override val id = identifier("tiny_shield")
		override val blocks = mapOf(
			Pos(0, 0, 0) match Blocks.DIAMOND_BLOCK,
			Pos(1, 0, 0) matchTag BlockTags.IMPERMEABLE,
			Pos(-1, 0, 0) matchTag BlockTags.IMPERMEABLE,
			Pos(0, 0, 1).matchAny(BlockType.of(Blocks.IRON_BLOCK), BlockType.of(Blocks.GOLD_BLOCK)),
			Pos(0, 0, -1) matchTag BlockTags.IMPERMEABLE
		)

		override val dataType = PowerableMultiblockData()

		override fun tick(instance: MultiblockInstance) {
			val data = (instance.data as PowerableMultiblockData)
			data.power += 1
		}
	}

	val SMALL_SHIELD = object : MultiblockType {
		override val displayName = "Small Shield"
		override val id = identifier("small_shield")
		override val blocks = mapOf(
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


		override val dataType = EmptyMultiblockData()

		override fun tick(instance: MultiblockInstance) {

		}
	}

	val DEBUG_FUEL_SOURCE = object : MultiblockType {
		override val displayName = "Fuel Source (debug)"
		override val id = identifier("debug_fuel_source")
		override val blocks = mapOf(
			Pos(0, 0, 0) match Blocks.BARREL,
			Pos(1, 0, 0) match Blocks.IRON_BLOCK,
			Pos(-1, 0, 0) match Blocks.IRON_BLOCK,
			Pos(0, 0, 1) match Blocks.IRON_BLOCK,
			Pos(0, 0, -1) match Blocks.IRON_BLOCK,
		)

		override val dataType = FuelableMultiblockData()

		val pipeContactPoint = OriginRelative(0, -1, 0)

		override fun tick(instance: MultiblockInstance) {
			(instance.data as FuelableMultiblockData).fuel = instance.data.capacity
			transferFuelWith(instance, pipeContactPoint)
		}
	}

	val DEBUG_FUEL_VOID = object : MultiblockType {
		override val displayName = "Fuel Void (debug)"
		override val id = identifier("debug_fuel_void")
		override val blocks = mapOf(
			Pos(0, 0, 0) match Blocks.BARREL,
			Pos(1, 0, 0) match Blocks.GOLD_BLOCK,
			Pos(-1, 0, 0) match Blocks.GOLD_BLOCK,
			Pos(0, 0, 1) match Blocks.GOLD_BLOCK,
			Pos(0, 0, -1) match Blocks.GOLD_BLOCK,
		)

		override val dataType = FuelableMultiblockData()

		val pipeContactPoint = OriginRelative(0, -1, 0)

		override fun tick(instance: MultiblockInstance) {
			(instance.data as FuelableMultiblockData).fuel = 0
			transferFuelWith(instance, pipeContactPoint)
		}
	}

	val DEBUG_FUEL_STORAGE = object : MultiblockType {
		override val displayName = "Fuel Storage (debug)"
		override val id = identifier("debug_fuel_storage")
		override val blocks = mapOf(
			Pos(0, 0, 0) match Blocks.BARREL,
			Pos(1, 0, 0) match Blocks.EMERALD_BLOCK,
			Pos(-1, 0, 0) match Blocks.EMERALD_BLOCK,
			Pos(0, 0, 1) match Blocks.EMERALD_BLOCK,
			Pos(0, 0, -1) match Blocks.EMERALD_BLOCK,
		)

		override val dataType = FuelableMultiblockData()

		val pipeContactPoint = OriginRelative(0, -1, 0)

		override fun tick(instance: MultiblockInstance) {
			transferFuelWith(instance, pipeContactPoint)
		}
	}


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
