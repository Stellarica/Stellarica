package io.github.hydrazinemc.hydrazine.starships

import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.plugin
import io.github.hydrazinemc.hydrazine.starships.StarshipBlockSetter.blockSetQueueQueue
import io.github.hydrazinemc.hydrazine.utils.BlockLocation
import io.github.hydrazinemc.hydrazine.utils.ChunkLocation
import io.github.hydrazinemc.hydrazine.utils.ConfigurableValues
import io.github.hydrazinemc.hydrazine.utils.ConnectionUtils
import io.github.hydrazinemc.hydrazine.utils.Tasks
import org.bukkit.Bukkit
import org.bukkit.ChunkSnapshot
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import kotlin.system.measureTimeMillis

class Starship(private val block: BlockLocation, private var world: World) {
	private var detectedBlocks = mutableSetOf<BlockLocation>()

	private var owner: Player? = null
	private var pilot: Player? = null

	var passengers = mutableSetOf<Entity>()
	var allowedBlocks = mutableSetOf<Material>()


	val blockCount: Int
		get() {
			return detectedBlocks.size
		}

	fun detectStarship(player: Player?) {
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
				val undetectables = ConfigurableValues.defaultUndetectable.toMutableSet() // Get a copy of all default undetectables
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
							player.sendMessage("Detection limit reached. (${ConfigurableValues.detectionLimit})")
							plugin.logger.info("Detection limit reached. (${ConfigurableValues.detectionLimit})")
							nextBlocksToCheck.clear()
							detectedBlocks.clear()
							break
						}

						detectedBlocks.add(currentBlock)

						val block1 = currentBlock.relative(1, 0, 0)
						val block2 = currentBlock.relative(-1, 0, 0)
						val block3 = currentBlock.relative(0, 1, 0)
						val block4 = currentBlock.relative(0, -1, 0)
						val block5 = currentBlock.relative(0, 0, 1)
						val block6 = currentBlock.relative(0, 0, -1)

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
	}

	fun deactivateStarship() {
		pilot = null
	}

	fun movePassengers(offset: BlockLocation) {
		val offsetLoc = offset.asLocation
		offsetLoc.world = world
		passengers.forEach {
			if (it is Player) {
				ConnectionUtils.teleport(it, it.location.add(offsetLoc))
			}
			else {
				it.teleport(it.location.add(offsetLoc))
			}
		}
	}

	fun queueMovement(offset: BlockLocation) {
		Tasks.async {
			// TODO: check if we've arrived before trying to move
			// TODO: handle unloaded chunks
			val chunkCache = mutableMapOf<ChunkLocation, ChunkSnapshot>()

			// Construct the undetectable list
			val undetectables =
				ConfigurableValues.defaultUndetectable.toMutableSet() // Get a copy of all default undetectables
			undetectables.addAll(ConfigurableValues.forcedUndetectable)               // Add all forced undetectables
			undetectables.removeAll(allowedBlocks)

			val newDetectedBlocks = mutableSetOf<BlockLocation>()

			val blocksToSet = mutableMapOf<BlockLocation, BlockData>()

			val airData = Bukkit.createBlockData(org.bukkit.Material.AIR)

			detectedBlocks.forEach { cBlock ->
				val cChunkCoord = ChunkLocation(cBlock.x shr 4, cBlock.z shr 4)

				val cBlockData = chunkCache.getOrPut(cChunkCoord) {
					world.getChunkAt(cChunkCoord.x, cChunkCoord.z).getChunkSnapshot(false, false, false)
				}.getBlockData(cBlock.x - (cChunkCoord.x shl 4), cBlock.y, cBlock.z - (cChunkCoord.z shl 4))

				val cMaterial = cBlockData.material

				// Step 1: Confirm that there is still a detectable block there.
				if (undetectables.contains(cMaterial)) return@forEach

				val tBlock = cBlock.relative(offset.x, offset.y, offset.z)

				val tChunkCoord = ChunkLocation(tBlock.x shr 4, tBlock.z shr 4)

				val tBlockData = chunkCache.getOrPut(tChunkCoord) {
					world.getChunkAt(tChunkCoord.x, tChunkCoord.z).getChunkSnapshot(false, false, false)
				}.getBlockData(tBlock.x - (tChunkCoord.x shl 4), tBlock.y, tBlock.z - (tChunkCoord.z shl 4))

				val tMaterial = tBlockData.material

				// Step 2: Confirm that we can move that block.
				if (detectedBlocks.contains(tBlock) || tBlockData.material.isAir) {

					// Step 3: If the current block has not already been replaced, set it to air.
					blocksToSet.putIfAbsent(cBlock, airData)

					// Step 4: Set the target block to the block data of the current block.
					blocksToSet[tBlock] = cBlockData

					// Step 5: Add the target block to the new detected blocks list.
					newDetectedBlocks.add(tBlock)

				} else {

					// The ship is blocked!
					pilot?.sendMessage("Blocked at " + tBlock.x + ", " + tBlock.y + ", " + tBlock.z + " by " + tMaterial)
					return

				}
			}

			if (detectedBlocks.size != newDetectedBlocks.size) pilot?.sendMessage("Lost " + (newDetectedBlocks.size - detectedBlocks.size) + " blocks!")

			detectedBlocks = newDetectedBlocks

			blockSetQueueQueue[blocksToSet] = this
		}
		movePassengers(offset)
	}
}
