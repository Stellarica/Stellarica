package io.github.petercrawley.minecraftstarshipplugin.ships

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin
import io.github.petercrawley.minecraftstarshipplugin.MSPLocation
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player

/*
	I am aware that this async task is not properly being shutdown when the plugin is reloaded.
	I will fix it later.

	TODO: Fix.
 */

class Starship(private val origin: MSPLocation, private val owner: Player) {
	private val detectedBlocks: MutableSet<MSPLocation> = mutableSetOf()

	fun detect() {
		Bukkit.getScheduler().runTaskAsynchronously(MinecraftStarshipPlugin.getPlugin(), Runnable {
			owner.sendMessage("Detecting Starship.")

			val checkedBlocks: MutableSet<MSPLocation> = mutableSetOf()
			val blocksToCheck: MutableSet<MSPLocation> = mutableSetOf()

			blocksToCheck.add(origin)

			while (blocksToCheck.isNotEmpty()) {
				if (detectedBlocks.size == 500000) {
					owner.sendMessage("Reached arbitrary detection limit. (500,000)")
					break
				}

				val currentBlock = blocksToCheck.first()
				blocksToCheck.remove(currentBlock)

				if (checkedBlocks.contains(currentBlock)) continue

				checkedBlocks.add(currentBlock)

				val type = currentBlock.block().type

				if (type == Material.AIR) continue

				if (MinecraftStarshipPlugin.getPlugin().nonDetectableBlocks.contains(type)) continue

				detectedBlocks.add(currentBlock)

				blocksToCheck.add(currentBlock.add( 1, 0, 0))
				blocksToCheck.add(currentBlock.add(-1, 0, 0))
				blocksToCheck.add(currentBlock.add( 0, 1, 0))
				blocksToCheck.add(currentBlock.add( 0,-1, 0))
				blocksToCheck.add(currentBlock.add( 0, 0, 1))
				blocksToCheck.add(currentBlock.add( 0, 0,-1))
			}

			owner.sendMessage("Detected " + detectedBlocks.size + " blocks.")
		})
	}

	private fun move(x: Int, y: Int, z: Int) {
		Bukkit.getScheduler().runTaskAsynchronously(MinecraftStarshipPlugin.getPlugin(), Runnable {
			for (block in detectedBlocks) {
				if (!detectedBlocks.contains(block.add(x, y, z))) {
					if (!block.add(x, y, z).block().type.isAir) {
						owner.sendMessage("Obstructed at " + block.x + ", " + block.y + ", " + block.z  + " by " + block.block().type.toString())
						return@Runnable
					}
				}
			}

			val blocksToUpdate: MutableMap<Block, BlockData> = mutableMapOf()

			// Start by getting all of the old positions and setting them to air.
			val airBlockData = Bukkit.getServer().createBlockData(Material.AIR)

			detectedBlocks.forEach {
				blocksToUpdate[it.block()] = airBlockData
			}

			// Now move all the blocks on out map.
			detectedBlocks.forEach {
				blocksToUpdate[it.add(x, y, z).block()] = it.block().blockData
			}

			Bukkit.getScheduler().runTask(MinecraftStarshipPlugin.getPlugin(), Runnable {
				// Remember we have not actually made any changes yet, but we now have a list of all the positions, and what they need to be changed too.
				// So lets actually move stuff now.
				blocksToUpdate.forEach {
					it.key.setBlockData(it.value, false)
				}
			})
		})
	}
}