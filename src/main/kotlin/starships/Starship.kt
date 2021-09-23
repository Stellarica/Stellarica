package io.github.petercrawley.minecraftstarshipplugin.starships

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.defaultUndetectable
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.forcedUndetectable
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.getPlugin
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.mainConfig
import io.github.petercrawley.minecraftstarshipplugin.customblocks.Material
import io.github.petercrawley.minecraftstarshipplugin.utils.BlockLocation
import io.github.petercrawley.minecraftstarshipplugin.utils.ChunkLocation
import org.bukkit.Bukkit
import org.bukkit.ChunkSnapshot
import org.bukkit.World
import org.bukkit.entity.Player
import kotlin.system.measureTimeMillis

class Starship(private val block: BlockLocation, private var world: World, private val player: Player) {
	private var detectedBlocks = mutableSetOf<BlockLocation>()
	private val owner = player

	fun detectStarship() {
		Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), Runnable {
			val time = measureTimeMillis {
				player.sendMessage("Detecting Starship")
				getPlugin().logger.info("Detecting Starship for " + player.name)

				detectedBlocks.add(block) // Add the interface to the ship

				val chunkCache = mutableMapOf<ChunkLocation, ChunkSnapshot>()

				var nextBlocksToCheck = detectedBlocks
				detectedBlocks = mutableSetOf()

				val checkedBlocks = nextBlocksToCheck.toMutableSet()

				// Construct the undetectable list
				val undetectables = defaultUndetectable.toMutableSet() // Get a copy of all default undetectables
				undetectables.addAll(forcedUndetectable)               // Add all forced undetectables

				val detectionLimit = mainConfig.getInt("detectionLimit", 500000)

				while (nextBlocksToCheck.size > 0) {
					val blocksToCheck = nextBlocksToCheck
					nextBlocksToCheck = mutableSetOf()

					for (currentBlock in blocksToCheck) {
						val chunkCoordinate = ChunkLocation(currentBlock.x shr 4, currentBlock.z shr 4)

						val chunk = chunkCache.getOrPut(chunkCoordinate) {
							world.getChunkAt(chunkCoordinate.x, chunkCoordinate.z).getChunkSnapshot(false, false, false)
						}

						val type = Material(chunk.getBlockData(currentBlock.x - (chunkCoordinate.x shl 4), currentBlock.y, currentBlock.z - (chunkCoordinate.z shl 4)))

						if (undetectables.contains(type)) continue

						if (detectedBlocks.size > detectionLimit) {
							player.sendMessage("Detection limit reached. ($detectionLimit)")
							getPlugin().logger.info("Detection limit reached. ($detectionLimit)")
							nextBlocksToCheck.clear()
							detectedBlocks.clear()
							break
						}

						detectedBlocks.add(currentBlock)

						val block1 = currentBlock.relative( 1,  0,  0)
						val block2 = currentBlock.relative(-1,  0,  0)
						val block3 = currentBlock.relative( 0,  1,  0)
						val block4 = currentBlock.relative( 0, -1,  0)
						val block5 = currentBlock.relative( 0,  0,  1)
						val block6 = currentBlock.relative( 0,  0, -1)

						if (!checkedBlocks.contains(block1)) {
							checkedBlocks.add(block1)
							nextBlocksToCheck.add(block1)
						}
						if (!checkedBlocks.contains(block2)) {
							checkedBlocks.add(block2)
							nextBlocksToCheck.add(block2)
						}
						if (!checkedBlocks.contains(block3)) {
							checkedBlocks.add(block2)
							nextBlocksToCheck.add(block2)
						}
						if (!checkedBlocks.contains(block4)) {
							checkedBlocks.add(block3)
							nextBlocksToCheck.add(block3)
						}
						if (!checkedBlocks.contains(block5)) {
							checkedBlocks.add(block4)
							nextBlocksToCheck.add(block4)
						}
						if (!checkedBlocks.contains(block6)) {
							checkedBlocks.add(block6)
							nextBlocksToCheck.add(block6)
						}
					}
				}
			}

			if (mainConfig.getBoolean("timeOperations", false)) {
				getPlugin().logger.info("Starship Detection took $time ms.")
			}

			player.sendMessage("Detected " + detectedBlocks.size + " blocks.")
			getPlugin().logger.info("Detected " + detectedBlocks.size + " blocks.")
		})
	}
}
