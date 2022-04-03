package io.github.hydrazinemc.hydrazine.starships

import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.activeStarships
import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.plugin
import io.github.hydrazinemc.hydrazine.starships.StarshipBlockSetter.blockSetQueueQueue
import io.github.hydrazinemc.hydrazine.utils.AlreadyMovingException
import io.github.hydrazinemc.hydrazine.utils.BlockLocation
import io.github.hydrazinemc.hydrazine.utils.ChunkLocation
import io.github.hydrazinemc.hydrazine.utils.ConfigurableValues
import io.github.hydrazinemc.hydrazine.utils.Tasks
import io.github.hydrazinemc.hydrazine.utils.nms.ConnectionUtils
import org.bukkit.Bukkit
import org.bukkit.ChunkSnapshot
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.system.measureTimeMillis

class Starship(private val block: BlockLocation, private var world: World) {
	private var detectedBlocks = mutableSetOf<BlockLocation>()

	private var owner: Player? = null
	var pilot: Player? = null
	var isMoving = false

	var passengers = mutableSetOf<Entity>()
	var allowedBlocks = mutableSetOf<Material>()

	var undetectables = mutableSetOf<Material>()


	val blockCount: Int
		get() {
			return detectedBlocks.size
		}

	fun detectStarship(player: Player?) {
		if (isMoving) throw AlreadyMovingException("Ship attempted to detect, but is currently moving!")
		Tasks.async {
			val time = measureTimeMillis {
				player?.sendMessage("Detecting Starship")
				plugin.logger.info("Detecting Starship for " + player?.name)

				detectedBlocks.add(block) // Add the interface to the ship

				val chunkCache = mutableMapOf<ChunkLocation, ChunkSnapshot>()

				var nextBlocksToCheck = detectedBlocks
				detectedBlocks = mutableSetOf()

				val checkedBlocks = nextBlocksToCheck.toMutableSet()

				// Construct the undetectable list
				val undetectables =
					ConfigurableValues.defaultUndetectable.toMutableSet() // Get a copy of all default undetectables
				undetectables.addAll(ConfigurableValues.forcedUndetectable)               // Add all forced undetectables
				undetectables.removeAll(allowedBlocks)

				while (nextBlocksToCheck.size > 0) {
					val blocksToCheck = nextBlocksToCheck
					nextBlocksToCheck = mutableSetOf()

					for (currentBlock in blocksToCheck) {
						val chunkCoordinate = ChunkLocation(currentBlock.x shr 4, currentBlock.z shr 4)

						val chunk = chunkCache.getOrPut(chunkCoordinate) {
							world.getChunkAt(chunkCoordinate.x, chunkCoordinate.z).getChunkSnapshot(false, false, false)
						}

						val type =
							chunk.getBlockData(
								currentBlock.x - (chunkCoordinate.x shl 4),
								currentBlock.y,
								currentBlock.z - (chunkCoordinate.z shl 4)
							).material


						if (undetectables.contains(type)) continue

						if (detectedBlocks.size > ConfigurableValues.detectionLimit) {
							player?.sendMessage("Detection limit reached. (${ConfigurableValues.detectionLimit})")
							plugin.logger.info("Detection limit reached. (${ConfigurableValues.detectionLimit})")
							nextBlocksToCheck.clear()
							detectedBlocks.clear()
							break
						}

						detectedBlocks.add(currentBlock)

						val block1 = currentBlock + BlockLocation(1, 0, 0, null)
						val block2 = currentBlock + BlockLocation(-1, 0, 0, null)
						val block3 = currentBlock + BlockLocation(0, 1, 0, null)
						val block4 = currentBlock + BlockLocation(0, -1, 0, null)
						val block5 = currentBlock + BlockLocation(0, 0, 1, null)
						val block6 = currentBlock + BlockLocation(0, 0, -1, null)

						if (!checkedBlocks.contains(block1)) {
							checkedBlocks.add(block1)
							nextBlocksToCheck.add(block1)
						}
						if (!checkedBlocks.contains(block2)) {
							checkedBlocks.add(block2)
							nextBlocksToCheck.add(block2)
						}
						if (!checkedBlocks.contains(block3)) {
							checkedBlocks.add(block3)
							nextBlocksToCheck.add(block3)
						}
						if (!checkedBlocks.contains(block4)) {
							checkedBlocks.add(block4)
							nextBlocksToCheck.add(block4)
						}
						if (!checkedBlocks.contains(block5)) {
							checkedBlocks.add(block5)
							nextBlocksToCheck.add(block5)
						}
						if (!checkedBlocks.contains(block6)) {
							checkedBlocks.add(block6)
							nextBlocksToCheck.add(block6)
						}
					}
				}
			}

			if (ConfigurableValues.timeOperations) {
				player?.sendMessage("Starship Detection took $time ms.")
				plugin.logger.info("Starship Detection took $time ms.")
			}

			player?.sendMessage("Detected " + detectedBlocks.size + " blocks.")
			plugin.logger.info("Detected " + detectedBlocks.size + " blocks.")
		}
	}

	fun activateStarship(pilot: Player) {
		// Determine passengers, pilot
		passengers.add(pilot)
		this.pilot = pilot
		activeStarships.add(this)
		updateUndetectables()
	}

	fun deactivateStarship() {
		pilot = null
		passengers.clear()
		activeStarships.remove(this)
	}

	fun movePassengers(offset: (BlockLocation) -> BlockLocation) {
		passengers.forEach {
			if (it is Player) {
				ConnectionUtils.teleport(it, offset(BlockLocation(it.location)).asLocation)
			} else {
				it.teleport(offset(BlockLocation(it.location)).asLocation)
			}
		}
	}

	fun updateUndetectables() {
		// Construct the undetectable list
		undetectables =
			ConfigurableValues.defaultUndetectable.toMutableSet() // Get a copy of all default undetectables
		undetectables.addAll(ConfigurableValues.forcedUndetectable)               // Add all forced undetectables
		undetectables.removeAll(allowedBlocks)
	}

	fun queueMovement(offset: BlockLocation) {
		queueChange({ current ->
			return@queueChange current + offset
		}, "Movement")
	}
	fun queueRotation(theta: Double) {
		val origin = BlockLocation(pilot!!.location) // TODO: fix
		queueChange({ current ->
			return@queueChange rotateCoordinates(current, origin, theta)
		}, "Rotation")
	}

	fun queueChange(modifier: (BlockLocation) -> BlockLocation, name: String) {
		if (isMoving) throw AlreadyMovingException("Starship attempted to queue movement, but it is already moving!")
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
					pilot?.sendMessage("One of your detected blocks is not pilotable! This is probably a bug.")
					return@forEach
				}


				val targetBlock = modifier(currentBlock)
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

					// Step 4: Set the target block to the block data of the current block.
					blocksToSet[targetBlock] = currentBlockData

					// Step 5: Add the target block to the new detected blocks list.
					newDetectedBlocks.add(targetBlock)

				} else {
					// The ship is blocked!
					pilot?.sendMessage("$name blocked at " + targetBlock.x + ", " + targetBlock.y + ", " + targetBlock.z + " by " + targetMaterial)
					return@async
				}
			}

			if (detectedBlocks.size != newDetectedBlocks.size) pilot?.sendMessage("Lost " + (newDetectedBlocks.size - detectedBlocks.size) + " blocks while queueing $name!")

			detectedBlocks = newDetectedBlocks
			blockSetQueueQueue[blocksToSet] = Pair(this, modifier)
		}
	}

	private fun rotateCoordinates(loc: BlockLocation, origin: BlockLocation, theta: Double): BlockLocation = BlockLocation(
		(origin.x + (loc.x - origin.x) * cos(theta) - (loc.y - origin.y) * sin(theta)).roundToInt(),
		loc.y,
		(origin.z + (loc.x - origin.x) * sin(theta) + (loc.z - origin.z) * cos(theta)).roundToInt(),
		loc.world
	)
}
