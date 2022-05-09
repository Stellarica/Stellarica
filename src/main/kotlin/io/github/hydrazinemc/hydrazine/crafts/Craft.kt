package io.github.hydrazinemc.hydrazine.crafts

import io.github.hydrazinemc.hydrazine.Hydrazine
import io.github.hydrazinemc.hydrazine.crafts.CraftBlockSetter.blockSetQueueQueue
import io.github.hydrazinemc.hydrazine.crafts.pilotable.Pilotable
import io.github.hydrazinemc.hydrazine.utils.AlreadyMovingException
import io.github.hydrazinemc.hydrazine.utils.BlockLocation
import io.github.hydrazinemc.hydrazine.utils.ChunkLocation
import io.github.hydrazinemc.hydrazine.utils.ConfigurableValues
import io.github.hydrazinemc.hydrazine.utils.RotationAmount
import io.github.hydrazinemc.hydrazine.utils.Tasks
import io.github.hydrazinemc.hydrazine.utils.Vector3
import io.github.hydrazinemc.hydrazine.utils.extensions.rotate
import io.github.hydrazinemc.hydrazine.utils.extensions.sendMiniMessage
import io.github.hydrazinemc.hydrazine.utils.nms.ConnectionUtils
import io.github.hydrazinemc.hydrazine.utils.rotateCoordinates
import org.bukkit.Bukkit
import org.bukkit.ChunkSnapshot
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import kotlin.system.measureTimeMillis

open class Craft(var origin: Location) {

	private var detectedBlocks = mutableSetOf<BlockLocation>()
	var isMoving = false
	var passengers = mutableSetOf<Entity>()
	var allowedBlocks = mutableSetOf<Material>()
	var undetectables = mutableSetOf<Material>()
	val blockCount: Int
		get() = detectedBlocks.size

	/**
	 * Message this craft's pilot, if it has one.
	 * If the ship isn't being piloted, message the owner.
	 * MiniMessage formatting is allowed
	 *
	 * @see messagePassengers
	 */
	fun messagePilot(message: String) {
		if (this is Pilotable) { pilot?.sendMiniMessage(message) ?: owner?.sendMiniMessage(message) }
	}

	/**
	 * Message all passengers of this craft.
	 * MiniMessage formatting is allowed
	 *
	 * @see messagePilot
	 */
	fun messagePassengers(message: String) {
		passengers.forEach { it.sendMiniMessage(message) }
	}

	/**
	 * Detect this craft
	 * @throws AlreadyMovingException
	 */
	fun detectCraft() {
		if (isMoving) throw AlreadyMovingException("Craft attempted to detect, but is currently moving!")
		messagePilot("<gray>Detecting craft...")
		Tasks.async {
			val time = measureTimeMillis {
				val chunkCache = mutableMapOf<ChunkLocation, ChunkSnapshot>()

				var nextBlocksToCheck = detectedBlocks
				nextBlocksToCheck.add(BlockLocation(origin))
				detectedBlocks = mutableSetOf()
				val checkedBlocks = nextBlocksToCheck.toMutableSet()

				updateUndetectables()

				while (nextBlocksToCheck.size > 0) {
					val blocksToCheck = nextBlocksToCheck
					nextBlocksToCheck = mutableSetOf()

					for (currentBlock in blocksToCheck) {
						val chunkCoordinate = ChunkLocation(currentBlock.x shr 4, currentBlock.z shr 4)

						val chunk = chunkCache.getOrPut(chunkCoordinate) {
							origin.world.getChunkAt(chunkCoordinate.x, chunkCoordinate.z).getChunkSnapshot(false, false, false)
						}

						val type =
							chunk.getBlockData(
								currentBlock.x - (chunkCoordinate.x shl 4),
								currentBlock.y,
								currentBlock.z - (chunkCoordinate.z shl 4)
							).material


						if (undetectables.contains(type)) continue

						if (detectedBlocks.size > ConfigurableValues.detectionLimit) {
							Hydrazine.plugin.logger.info("Detection limit reached. (${ConfigurableValues.detectionLimit})")
							messagePilot("<gold>Detection limit reached. (${ConfigurableValues.detectionLimit} blocks)")
							nextBlocksToCheck.clear()
							detectedBlocks.clear()
							break
						}

						detectedBlocks.add(currentBlock)

						// Slightly condensed from MSP's nonsense, but this could be improved
						for (x in -1..1) {
							for (y in -1..1) {
								for (z in -1..1) {
									if (x == y && z == y && y == 0) continue
									val block = currentBlock + BlockLocation(x, y, z, null)
									if (!checkedBlocks.contains(block)) {
										checkedBlocks.add(block)
										nextBlocksToCheck.add(block)
									}
								}
							}
						}
					}
				}
			}
			messagePilot("<green>Craft detected! (${detectedBlocks.size} blocks)")
			messagePilot("<gray>Detected ${detectedBlocks.size} blocks in ${time}ms. (${detectedBlocks.size / time} blocks/ms)")
		}
	}

	/**
	 * Move all passengers by offset.
	 * Uses bukkit to teleport entities, and NMS to move players.
	 */
	fun movePassengers(offset: (Vector3) -> Vector3, rotation: RotationAmount = RotationAmount.NONE) {
		passengers.forEach {
			if (it is Player) {
				ConnectionUtils.teleportRotate(it, offset(Vector3(it.location)).asLocation, rotation)
			} else {
				it.teleport(offset(Vector3(it.location)).asLocation)
			}
		}
	}


	/**
	 * Update this craft's undetectables.
	 */
	fun updateUndetectables() {
		// Construct the undetectable list
		undetectables =
			ConfigurableValues.defaultUndetectable.toMutableSet() // Get a copy of all default undetectables
		undetectables.addAll(ConfigurableValues.forcedUndetectable)               // Add all forced undetectables
		undetectables.removeAll(allowedBlocks)
		messagePilot("<gray>Updated craft's undetectable blocks.")
	}

	/**
	 * Translate the craft by [offset] blocks
	 * @throws AlreadyMovingException if craft movement is currently queued.
	 * @see queueChange
	 */
	fun queueMovement(offset: BlockLocation) {
		queueChange({ current ->
			return@queueChange current + Vector3(offset)
		}, "Movement", offset.world!!)
	}

	/**
	 * Move the craft to another world
	 * @throws AlreadyMovingException if craft movement is currently queued.
	 * @see queueChange
	 */
	fun queueWorldChange() {
		TODO()
	}

	/**
	 * Rotate the craft and contents by [rotation]
	 * @throws AlreadyMovingException if craft movement is currently queued.
	 * @see queueChange
	 */
	fun queueRotation(rotation: RotationAmount) {
		queueChange({ current ->
			return@queueChange rotateCoordinates(current, Vector3(origin), rotation)
		}, "Rotation", origin.world, rotation)
	}

	/**
	 * Queue a starships move in [StarshipBlockSetter].
	 * Asynchronously calculates where the ship needs to move to, and queues
	 * block placements in [blockSetQueueQueue].
	 *
	 * @param modifier the function through which block coordinates are passed
	 * @param name the name of the change (e.g "Rotation" or "Translation")
	 * @param world the world in which to place the target blocks
	 * @param rotation the rotation applied. Not used to move blocks, but rotates entities and rotational blocks
	 * @see queueMovement
	 * @see queueWorldChange
	 * @see queueRotation
	 * @throws AlreadyMovingException if this ship already has movement queued.
	 */
	private fun queueChange(
		modifier: (Vector3) -> Vector3,
		name: String,
		world: World,
		rotation: RotationAmount = RotationAmount.NONE
	) {
		if (isMoving) throw AlreadyMovingException("Craft attempted to queue movement, but it is already moving!")
		isMoving = true
		Tasks.async {
			// TODO: handle unloaded chunks
			val chunkCache = mutableMapOf<ChunkLocation, ChunkSnapshot>()

			val newDetectedBlocks = mutableSetOf<BlockLocation>()

			val blocksToSet = mutableMapOf<BlockLocation, BlockData>()

			val airData = Bukkit.createBlockData(Material.AIR)

			detectedBlocks.forEach { currentBlock ->
				val currentChunkCoord = ChunkLocation(currentBlock.x shr 4, currentBlock.z shr 4)
				val currentBlockData = chunkCache.getOrPut(currentChunkCoord) {
					world.getChunkAt(currentChunkCoord.x, currentChunkCoord.z).getChunkSnapshot(false, false, false)
				}.getBlockData(
					currentBlock.x - (currentChunkCoord.x shl 4),
					currentBlock.y,
					currentBlock.z - (currentChunkCoord.z shl 4)
				)
				val currentMaterial = currentBlockData.material

				// Step 1: Confirm that there is still a detectable block there.
				if (undetectables.contains(currentMaterial)) {
					messagePilot("<red>Skipping undetectable block at (${currentBlock.x},${currentBlock.y},${currentBlock.z}) - $currentMaterial.")
					messagePassengers("<bold><gold>This is a bug, please report it.")
					// TODO: warn or throw something
					return@forEach
				}

				// todo: don't repeat this over here
				val targetBlock = modifier(Vector3(currentBlock)).asBlockLocation
				targetBlock.world = world
				val targetChunkCoord = ChunkLocation(targetBlock.x shr 4, targetBlock.z shr 4)
				val targetBlockData = chunkCache.getOrPut(targetChunkCoord) {
					world.getChunkAt(targetChunkCoord.x, targetChunkCoord.z).getChunkSnapshot(false, false, false)
				}.getBlockData(
					targetBlock.x - (targetChunkCoord.x shl 4),
					targetBlock.y,
					targetBlock.z - (targetChunkCoord.z shl 4)
				)
				val targetMaterial = targetBlockData.material

				// Step 2: Confirm that we can move that block.
				if (detectedBlocks.contains(targetBlock) || targetBlockData.material.isAir) {

					// Step 3: If the current block has not already been replaced, set it to air.
					blocksToSet.putIfAbsent(currentBlock, airData)

					// Make sure we have rotated if we need to
					currentBlockData.rotate(rotation)

					// Step 4: Set the target block to the block data of the current block.
					blocksToSet[targetBlock] = currentBlockData

					// Step 5: Add the target block to the new detected blocks list.
					if (!newDetectedBlocks.add(targetBlock)) Hydrazine.plugin.logger.warning("a newly detected block was overwritten while queueing $name!")

				} else {
					// The ship is blocked!
					messagePilot("<gold>$name blocked by $targetMaterial at <bold>(${targetBlock.x}, ${targetBlock.y}, ${targetBlock.z}</bold>)!")
					return@async
				}
			}

			if (detectedBlocks.size != newDetectedBlocks.size) {
				messagePassengers("<red>Lost <bold>${detectedBlocks.size - newDetectedBlocks.size}</bold> blocks while queuing $name!")
				messagePassengers("<bold><gold>This is a bug, please report it.")
			}

			detectedBlocks = newDetectedBlocks
			blockSetQueueQueue[blocksToSet] = CraftMoveData(this, modifier, rotation)
		}
	}
}