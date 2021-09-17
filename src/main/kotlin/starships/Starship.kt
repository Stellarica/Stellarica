package io.github.petercrawley.minecraftstarshipplugin.starships

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.defaultUndetectable
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.forcedUndetectable
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.getPlugin
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.mainConfig
import io.github.petercrawley.minecraftstarshipplugin.customblocks.MSPMaterial
import org.bukkit.Bukkit
import org.bukkit.ChunkSnapshot
import org.bukkit.block.Block
import org.bukkit.entity.Player

class Starship(origin: Block, private val user: Player) {
	val owner = user

	var pilot: Player? = null

	var detectedBlocks = mutableSetOf(MSPBlockLocation(origin)) // Blocks that we know are part of the ship.

	var allowedBlocks = setOf<MSPMaterial>() // Blocks that have been specifically allowed.

	fun detect() {
		Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), Runnable {
			val startTime = System.currentTimeMillis() // Debug

			user.sendMessage("Detecting Starship.")

			val chunkStorage = mutableMapOf<MSPChunkLocation, ChunkSnapshot>()

			val checkedBlocks = mutableSetOf<MSPBlockLocation>() // List of blocks we have checked
			val blocksToCheck = mutableSetOf<MSPBlockLocation>() // List of blocks we need to check

			blocksToCheck.addAll(detectedBlocks) // We need to check that all the blocks we already know about

			// Construct the undetectable list
			val undetectables = mutableSetOf<MSPMaterial>()
			undetectables.addAll(forcedUndetectable)
			undetectables.addAll(defaultUndetectable)
			undetectables.removeAll(allowedBlocks)

			// Get the detection limit from the config file.
			val detectionLimit = mainConfig.getInt("detectionLimit", 500000)

			while (blocksToCheck.isNotEmpty()) {
				if (detectedBlocks.size > detectionLimit) {
					user.sendMessage("Reached arbitrary detection limit. ($detectionLimit)")

					// Debug
					val endTime = System.currentTimeMillis()
					if (mainConfig.getBoolean("timeOperations", false)) {
						getPlugin().logger.info("Ship detection took: " + (endTime - startTime) + "ms.")
						user.sendMessage("Ship detection took: " + (endTime - startTime) + "ms.")
					}

					return@Runnable
				}

				// Get and remove the first item
				val currentBlock = blocksToCheck.first()
				blocksToCheck.remove(currentBlock)

				val chunkCoord = MSPChunkLocation(currentBlock)

				val chunk = chunkStorage.getOrPut(chunkCoord) {
					currentBlock.world.getChunkAt(chunkCoord.x, chunkCoord.z).chunkSnapshot
				}

				val type = MSPMaterial(chunk.getBlockData(currentBlock.x - (chunkCoord.x shl 4), currentBlock.y, currentBlock.z - (chunkCoord.z shl 4)))

				if (undetectables.contains(type)) continue

				detectedBlocks.add(currentBlock)

				// List of neighbouring blocks.
				mutableSetOf(
					currentBlock.relative( 1, 0, 0),
					currentBlock.relative(-1, 0, 0),
					currentBlock.relative( 0, 1, 0),
					currentBlock.relative( 0,-1, 0),
					currentBlock.relative( 0, 0, 1),
					currentBlock.relative( 0, 0,-1)

				// If it's not a block we have checked, check it
				).forEach {
					if (!checkedBlocks.contains(it)) {
						checkedBlocks.add(it)
						blocksToCheck.add(it)
					}
				}
			}

			user.sendMessage("Detected " + detectedBlocks.size + " blocks.")

			// Debug
			val endTime = System.currentTimeMillis()
			if (mainConfig.getBoolean("timeOperations", false)) {
				getPlugin().logger.info("Ship detection took: " + (endTime - startTime) + "ms.")
				user.sendMessage("Ship detection took: " + (endTime - startTime) + "ms.")
			}
		})
	}
}