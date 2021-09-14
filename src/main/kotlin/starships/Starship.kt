package io.github.petercrawley.minecraftstarshipplugin.starships

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.defaultUndetectable
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.forcedUndetectable
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.getPlugin
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.mainConfig
import io.github.petercrawley.minecraftstarshipplugin.customblocks.MSPMaterial
import io.github.petercrawley.minecraftstarshipplugin.starships.StarshipManager.blockMoves
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player

class Starship(origin: Block, private val pilot: Player) {
	val owner = pilot

	private val detectedBlocks = mutableSetOf(origin) // Blocks that we know are part of the ship.

	var allowedBlocks = setOf<MSPMaterial>() // Blocks that have been specifically allowed.

	fun detect() {
		Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), Runnable {
			val startTime = System.currentTimeMillis() // Debug

			pilot.sendMessage("Detecting Starship.")

			val checkedBlocks = mutableSetOf<Block>() // List of blocks we have checked
			val blocksToCheck = mutableSetOf<Block>() // List of blocks we need to check

			blocksToCheck.addAll(detectedBlocks) // We need to check that all the blocks we already know about

			// Construct the undetectable list
			val undetectables = mutableSetOf<MSPMaterial>()
			undetectables.addAll(forcedUndetectable)
			undetectables.addAll(defaultUndetectable)
			undetectables.removeAll(allowedBlocks)

			// Get the detection limit from the config file.
			val detectionLimit = mainConfig.getInt("detectionLimit", 500000)

			while (blocksToCheck.isNotEmpty()) {
				if (detectedBlocks.size == detectionLimit) {
					pilot.sendMessage("Reached arbitrary detection limit. ($detectionLimit)")
					break
				}

				// Get and remove the first item
				val currentBlock = blocksToCheck.first()
				blocksToCheck.remove(currentBlock)

				val type = MSPMaterial(currentBlock)

				if (undetectables.contains(type)) continue

				detectedBlocks.add(currentBlock)

				// List of neighbouring blocks.
				mutableSetOf(
					currentBlock.getRelative( 1, 0, 0),
					currentBlock.getRelative(-1, 0, 0),
					currentBlock.getRelative( 0, 1, 0),
					currentBlock.getRelative( 0,-1, 0),
					currentBlock.getRelative( 0, 0, 1),
					currentBlock.getRelative( 0, 0,-1)

				// If it's not a block we have checked, check it
				).forEach {
					if (!checkedBlocks.contains(it)) {
						checkedBlocks.add(it)
						blocksToCheck.add(it)
					}
				}
			}

			pilot.sendMessage("Detected " + detectedBlocks.size + " blocks.")

			// Debug
			val endTime = System.currentTimeMillis()
			if (mainConfig.getBoolean("timeOperations", false)) {
				getPlugin().logger.info("Ship detection took: " + (endTime - startTime) + "ms.")
			}
		})
	}

	private fun move(x: Int, y: Int, z: Int) {
		Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), Runnable {
			val startTime = System.currentTimeMillis() // Debug

			detectedBlocks.forEach {
				val targetBlock = it.getRelative(x, y, z)

				if (!detectedBlocks.contains(targetBlock)) {
					if (!targetBlock.type.isAir) {
						pilot.sendMessage("Obstructed at " + targetBlock.x + ", " + targetBlock.y + ", " + targetBlock.z  + " by " + targetBlock.type.toString())
						return@Runnable
					}
				}
			}

			val blocksToUpdate: MutableMap<Block, BlockData> = mutableMapOf()

			val airBlockData = Bukkit.getServer().createBlockData(Material.AIR)

			detectedBlocks.forEach {
				blocksToUpdate[it] = airBlockData
				blocksToUpdate[it.getRelative(x, y, z)] = it.blockData
			}

			// Remember we have not actually made any changes yet, but we now have a list of all the positions, and what they need to be changed too.
			blocksToUpdate.forEach{
				blockMoves[it.key] = it.value
			}

			val endTime = System.currentTimeMillis()

			if (mainConfig.getBoolean("timeOperations", false)) {
				getPlugin().logger.info("Ship movement took: " + (endTime - startTime) + "ms.")
			}
		})
	}
}