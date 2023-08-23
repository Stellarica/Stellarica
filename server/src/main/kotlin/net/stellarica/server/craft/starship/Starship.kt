package net.stellarica.server.craft.starship

import net.stellarica.common.coordinate.BlockPosition
import net.stellarica.common.util.toBlockPos
import net.stellarica.server.ConfigurableValues
import net.stellarica.server.craft.BasicCraft
import net.stellarica.server.craft.CraftContainer
import net.stellarica.server.craft.CraftTransformation
import net.stellarica.server.craft.Pilotable
import net.stellarica.server.craft.SimpleCraftContainer
import net.stellarica.server.craft.SimplePilotable

class Starship : BasicCraft(), Pilotable by SimplePilotable(), CraftContainer by SimpleCraftContainer() {
	override fun contains(block: BlockPosition): Boolean {
		return super.contains(block) || subcraftsContain(block)// check subcrafts
	}

	override fun transform(transformation: CraftTransformation): Boolean {
		// todo: add subcrafts to detected blocks
		if (!super.transform(transformation)) return false // and handle
		// todo: remove subcrafts from detected blocks
		movePassengers(this, transformation)
		return true
	}

	fun detect() {
		var nextBlocksToCheck = mutableSetOf(origin)
		detectedBlocks = mutableListOf()
		val checkedBlocks = nextBlocksToCheck.toMutableSet() // set for .contains performance

		while (nextBlocksToCheck.size > 0) {
			val blocksToCheck = nextBlocksToCheck
			nextBlocksToCheck = mutableSetOf()

			for (currentBlock in blocksToCheck) {

				val state = world.getBlockState(currentBlock.toBlockPos())
				if (state.isAir) continue

				if (detectedBlocks.size > ConfigurableValues.maxShipBlockCount) {
					nextBlocksToCheck.clear()
					detectedBlocks.clear()
					break
				}
				detectedBlocks.add(currentBlock)

				// Slightly condensed from MSP's nonsense, but this could be improved
				for (x in listOf(-1, 1)) {
					val block = currentBlock + BlockPosition(x, 0, 0)
					if (!checkedBlocks.contains(block)) {
						nextBlocksToCheck.add(block)
					}
				}
				for (z in listOf(-1, 1)) {
					val block = currentBlock + BlockPosition(0, 0, z)
					if (!checkedBlocks.contains(block)) {
						nextBlocksToCheck.add(block)
					}
				}
				for (y in -1..1) {
					val block = currentBlock + BlockPosition(0, y, 0)
					if (!checkedBlocks.contains(block)) {
						checkedBlocks.add(block)
						nextBlocksToCheck.add(block)
					}
				}
			}
		}
	}
}