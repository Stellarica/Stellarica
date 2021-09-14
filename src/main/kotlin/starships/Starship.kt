package io.github.petercrawley.minecraftstarshipplugin.starships

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.defaultUndetectable
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.forcedUndetectable
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.getPlugin
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.mainConfig
import io.github.petercrawley.minecraftstarshipplugin.customblocks.MSPMaterial
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player

class Starship(origin: Block, var owner: Player) {
	private val detectedBlocks = mutableSetOf(origin) // Blocks that we know are part of the ship.

	var allowedBlocks = setOf<MSPMaterial>() // Blocks that have been specifically allowed.

	fun detect() {
		Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), Runnable {
			val startTime = System.currentTimeMillis() // Debug

			owner.sendMessage("Detecting Starship.")

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
					owner.sendMessage("Reached arbitrary detection limit. ($detectionLimit)")
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

			owner.sendMessage("Detected " + detectedBlocks.size + " blocks.")

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
						owner.sendMessage("Obstructed at " + targetBlock.x + ", " + targetBlock.y + ", " + targetBlock.z  + " by " + targetBlock.type.toString())
						return@Runnable
					}
				}
			}

			val blocksToUpdate: MutableMap<Block, BlockData> = mutableMapOf()

			// Start by getting all the old positions and setting them to air.
			val airBlockData = Bukkit.getServer().createBlockData(Material.AIR)

			detectedBlocks.forEach {
				blocksToUpdate[it] = airBlockData
			}

			// Now move all the blocks on out map.
			detectedBlocks.forEach {
				blocksToUpdate[it.getRelative(x, y, z)] = it.blockData
			}

			Bukkit.getScheduler().runTask(getPlugin(), Runnable {
				// Remember we have not actually made any changes yet, but we now have a list of all the positions, and what they need to be changed too.
				// So lets actually move stuff now.
				blocksToUpdate.forEach {
					it.key.setBlockData(it.value, false)
				}

				val endTime = System.currentTimeMillis()

				if (mainConfig.getBoolean("timeOperations", false)) {
					getPlugin().logger.info("Ship movement took: " + (endTime - startTime) + "ms.")
				}
			})
		})
	}
}