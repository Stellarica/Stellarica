package net.stellarica.server.craft.starship

import io.papermc.paper.entity.TeleportFlag
import net.minecraft.core.Direction
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.phys.Vec3
import net.stellarica.common.coordinate.BlockPosition
import net.stellarica.common.util.asDegrees
import net.stellarica.common.util.rotateCoordinates
import net.stellarica.common.util.toBlockPos
import net.stellarica.common.util.toVec3
import net.stellarica.server.ConfigurableValues
import net.stellarica.server.craft.BasicCraft
import net.stellarica.server.craft.Craft
import net.stellarica.server.craft.CraftContainer
import net.stellarica.server.craft.CraftTransformation
import net.stellarica.server.craft.Rideable
import net.stellarica.server.craft.Subcraft
import net.stellarica.server.multiblock.MultiblockHandler
import net.stellarica.server.util.extension.minus
import net.stellarica.server.util.extension.plus
import net.stellarica.server.util.extension.toBlockPosition
import net.stellarica.server.util.extension.toLocation
import net.stellarica.server.util.extension.toVec3
import net.stellarica.server.util.wrapper.ServerWorld
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent

class Starship : BasicCraft(), CraftContainer, Rideable {

	override val passengers = mutableSetOf<LivingEntity>()
	override val subcrafts = mutableSetOf<Subcraft>()

	override fun addPassenger(passenger: LivingEntity) {
		passengers.add(passenger)
	}

	override fun removePassenger(passenger: LivingEntity) {
		passengers.remove(passenger)
	}

	var pilot: Player? = null
		private set

	fun setup(origin: BlockPosition, world: ServerWorld, orientation: Direction = Direction.NORTH) {
		this.origin = origin
		this.world = world
		this.orientation = orientation
	}

	private fun movePassengers(craft: Craft, data: CraftTransformation) {
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
					craft.origin.toVec3().add(Vec3(0.5, 0.0, 0.5)), data.rotation
				)
				else {
					val o = passenger.location.toVec3().minus(passenger.location.toBlockLocation().toVec3())
					(data.offset(passenger.location.toBlockPosition()).toVec3() + o)
				}.toLocation(craft.world)


			destination.world = data.world.bukkit
			destination.pitch = passenger.location.pitch
			destination.yaw = (passenger.location.yaw + data.rotation.asDegrees).toFloat()

			passenger.teleport(
				destination,
				PlayerTeleportEvent.TeleportCause.PLUGIN,
				TeleportFlag.EntityState.RETAIN_OPEN_INVENTORY,
				*TeleportFlag.Relative.entries.toTypedArray()
			)
		}
	}

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
		// todo: I don't like detect being in Starship... feels bad

		multiblocks.clear() // todo: <- not the right place for this, and remove never gets called
		detectedBlocks = mutableSetOf()
		val nextBlocksToCheck = mutableSetOf(origin)
		val blocksToCheck = mutableSetOf<BlockPosition>()
		val checkedBlocks = mutableSetOf<BlockPosition>()

		while (nextBlocksToCheck.isNotEmpty()) {

			checkedBlocks.addAll(blocksToCheck)
			blocksToCheck.clear()
			blocksToCheck.addAll(nextBlocksToCheck)
			nextBlocksToCheck.clear()

			for (currentBlock in blocksToCheck) {
				val state = world.vanilla.getBlockState(currentBlock.toBlockPos())

				if (state.isAir) continue
				detectedBlocks.add(currentBlock)

				if (detectedBlocks.size > ConfigurableValues.maxShipBlockCount) {
					detectedBlocks.clear()
					return
				}

				// Honestly, doing this with loops is worse
				val blocks = arrayOf(
					currentBlock + BlockPosition(0, 1, 0),
					currentBlock + BlockPosition(0, -1, 0),
					currentBlock + BlockPosition(1, 0, 0),
					currentBlock + BlockPosition(-1, 0, 0),
					currentBlock + BlockPosition(0, 0, 1),
					currentBlock + BlockPosition(0, 0, -1),
				)

				nextBlocksToCheck.addAll(blocks.filterNot { checkedBlocks.contains(it) })
			}
		}

		// probably a bad idea to go through all loaded multiblocks in the long run
		// todo: what about multiblocks that contain undetectable blocks?
		MultiblockHandler.getAllLoaded()
			.filter { it.world == world && contains(it.origin) }
			.forEach { addMultiblock(it) }
	}

	override fun addSubCraft(craft: Subcraft) {
		subcrafts.add(craft)
	}

	override fun removeSubCraft(craft: Subcraft) {
		subcrafts.remove(craft)
	}

	override fun subcraftsContain(block: BlockPosition): Boolean {
		for (craft in subcrafts) {
			if (craft.contains(block)) return true
		}
		return false
	}
}
