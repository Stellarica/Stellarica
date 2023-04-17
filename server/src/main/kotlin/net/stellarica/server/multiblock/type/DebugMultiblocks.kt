package net.stellarica.server.multiblock.type

import net.minecraft.world.level.block.Blocks
import net.stellarica.common.util.OriginRelative
import net.stellarica.server.StellaricaServer
import net.stellarica.server.multiblock.data.FuelableMultiblockData
import net.stellarica.server.multiblock.MultiblockInstance
import net.stellarica.server.multiblock.MultiblockType

@Suppress("unused")
object DebugMultiblocks : MultiblockDef() {
	val DEBUG_FUEL_SOURCE = object : MultiblockType() {
		override val displayName = "Fuel Source (debug)"
		override val id = StellaricaServer.identifier("debug_fuel_source")
		override val blocks = mapOf(
			pos(0, 0, 0) match Blocks.BARREL,
			pos(1, 0, 0) match Blocks.IRON_BLOCK,
			pos(-1, 0, 0) match Blocks.IRON_BLOCK,
			pos(0, 0, 1) match Blocks.IRON_BLOCK,
			pos(0, 0, -1) match Blocks.IRON_BLOCK,
		)

		override val dataType = FuelableMultiblockData()

		val pipeContactPoint = OriginRelative(0, -1, 0)

		override fun tick(instance: MultiblockInstance) {
			(instance.data as FuelableMultiblockData).fuel = instance.data.capacity
			FuelableMultiblockData.transferFuelWith(instance, pipeContactPoint)
		}
	}

	val DEBUG_FUEL_VOID = object : MultiblockType() {
		override val displayName = "Fuel Void (debug)"
		override val id = StellaricaServer.identifier("debug_fuel_void")
		override val blocks = mapOf(
			pos(0, 0, 0) match Blocks.BARREL,
			pos(1, 0, 0) match Blocks.GOLD_BLOCK,
			pos(-1, 0, 0) match Blocks.GOLD_BLOCK,
			pos(0, 0, 1) match Blocks.GOLD_BLOCK,
			pos(0, 0, -1) match Blocks.GOLD_BLOCK,
		)

		override val dataType = FuelableMultiblockData()

		val pipeContactPoint = OriginRelative(0, -1, 0)

		override fun tick(instance: MultiblockInstance) {
			(instance.data as FuelableMultiblockData).fuel = 0
			FuelableMultiblockData.transferFuelWith(instance, pipeContactPoint)
		}
	}

	val DEBUG_FUEL_STORAGE = object : MultiblockType() {
		override val displayName = "Fuel Storage (debug)"
		override val id = StellaricaServer.identifier("debug_fuel_storage")
		override val blocks = mapOf(
			pos(0, 0, 0) match Blocks.BARREL,
			pos(1, 0, 0) match Blocks.EMERALD_BLOCK,
			pos(-1, 0, 0) match Blocks.EMERALD_BLOCK,
			pos(0, 0, 1) match Blocks.EMERALD_BLOCK,
			pos(0, 0, -1) match Blocks.EMERALD_BLOCK,
		)

		override val dataType = FuelableMultiblockData()

		val pipeContactPoint = OriginRelative(0, -1, 0)

		override fun tick(instance: MultiblockInstance) {
			FuelableMultiblockData.transferFuelWith(instance, pipeContactPoint)
		}
	}
}