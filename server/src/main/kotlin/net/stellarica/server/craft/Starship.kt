package net.stellarica.server.craft

import io.papermc.paper.entity.TeleportFlag
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.phys.Vec3
import net.stellarica.common.coordinate.BlockPosition
import net.stellarica.common.util.asDegrees
import net.stellarica.common.util.rotateCoordinates
import net.stellarica.common.util.toBlockPos
import net.stellarica.common.util.toVec3
import net.stellarica.server.ConfigurableValues
import net.stellarica.server.util.extension.minus
import net.stellarica.server.util.extension.plus
import net.stellarica.server.util.extension.toBlockPosition
import net.stellarica.server.util.extension.toLocation
import net.stellarica.server.util.extension.toVec3
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent

class Starship : BasicCraft(), Rideable, CraftContainer, Pilotable {

	override var pilot: Player? = null
		private set
	override val passengers: MutableSet<LivingEntity> = mutableSetOf()

	override fun addPassenger(passenger: LivingEntity) {
		passengers.add(passenger)
	}

	override fun removePassenger(passenger: LivingEntity) {
		passengers.remove(passenger)
	}

	override fun pilot(pilot: Player) {
		this.pilot = pilot
	}

	override fun unpilot() {
		this.pilot = null
	}

	override fun addSubCraft(craft: Craft) {
		TODO("Not yet implemented")
	}

	override fun removeSubCraft(craft: Craft) {
		TODO("Not yet implemented")
	}

	override fun contains(block: BlockPosition): Boolean {
		return super.contains(block) || TODO() // check subcrafts
	}

	override fun transform(transformation: CraftTransformation): Boolean {
		// todo: add subcrafts to detected blocks
		if (!super.transform(transformation)) return false // and handle
		// todo: remove subcrafts from detected blocks
		movePassengers(transformation)
		return true
	}

	private fun movePassengers(data: CraftTransformation) {
		for (passenger in passengers) {
			// TODO: FIX
			// this is not a good solution because if there is any rotation, the player will not be translated by the offset
			// The result is that any ship movement that attempts to rotate and move in the same action will break.
			// For now there aren't any actions like that, but if there are in the future, this will need to be fixed.
			//
			// Rotating the whole ship around the adjusted origin will not work,
			// as rotating the ship 4 times does not bring it back to the original position
			//
			// However, without this dumb fix players do not rotate to the proper relative location
			val destination =
				if (data.rotation != Rotation.NONE) rotateCoordinates(
					passenger.location.toVec3(),
					origin.toVec3().add(Vec3(0.5, 0.0, 0.5)), data.rotation
				)
				else {
					val o = passenger.location.toVec3().minus(passenger.location.toBlockLocation().toVec3())
					(data.offset(passenger.location.toBlockPosition()).toVec3() + o)
				}.toLocation(world.world)


			destination.world = data.world.world
			destination.pitch = passenger.location.pitch
			destination.yaw = (passenger.location.yaw + data.rotation.asDegrees).toFloat()

			@Suppress("UnstableApiUsage")
			passenger.teleport(
				destination,
				PlayerTeleportEvent.TeleportCause.PLUGIN,
				TeleportFlag.EntityState.RETAIN_OPEN_INVENTORY,
				*TeleportFlag.Relative.entries.toTypedArray()
			)
		}
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