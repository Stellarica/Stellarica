package io.github.petercrawley.minecraftstarshipplugin

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

/*
	I am aware that this async task is not properly being shutdown when the plugin is reloaded.
	I will fix it later.

	TODO: Fix.
 */

class Starship(private val origin: Block, private val owner: Player) {
	private val detectedBlocks = mutableSetOf<Block>()

	fun detect() {
		Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), Runnable {
			val startTime = System.currentTimeMillis()

			owner.sendMessage("Detecting Starship.")

			val checkedBlocks = mutableSetOf<Block>()
			val blocksToCheck = mutableSetOf<Block>()

			blocksToCheck.add(origin)

			val undetectableMutable = mutableSetOf<MSPMaterial>()
			undetectableMutable.addAll(forcedUndetectable)
			undetectableMutable.addAll(defaultUndetectable)

			val undetectable = undetectableMutable.toSet()

			val detectionLimit = mainConfig.getInt("detectionLimit", 500000)

			while (blocksToCheck.isNotEmpty()) {
				if (detectedBlocks.size == detectionLimit) {
					owner.sendMessage("Reached arbitrary detection limit. ($detectionLimit)")
					break
				}

				val currentBlock = blocksToCheck.first()
				blocksToCheck.remove(currentBlock)

				if (checkedBlocks.contains(currentBlock)) continue

				checkedBlocks.add(currentBlock)

				val type = MSPMaterial(currentBlock)

				if (undetectable.contains(type)) continue

				detectedBlocks.add(currentBlock)

				blocksToCheck.add(currentBlock.getRelative( 1, 0, 0))
				blocksToCheck.add(currentBlock.getRelative(-1, 0, 0))
				blocksToCheck.add(currentBlock.getRelative( 0, 1, 0))
				blocksToCheck.add(currentBlock.getRelative( 0,-1, 0))
				blocksToCheck.add(currentBlock.getRelative( 0, 0, 1))
				blocksToCheck.add(currentBlock.getRelative( 0, 0,-1))
			}

			owner.sendMessage("Detected " + detectedBlocks.size + " blocks.")

			val endTime = System.currentTimeMillis()

			if (mainConfig.getBoolean("timeOperations", false)) {
				getPlugin().logger.info("Ship detection took: " + (endTime - startTime) + "ms.")
			}
		})
	}

	private fun move(x: Int, y: Int, z: Int) {
		Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), Runnable {
			val startTime = System.currentTimeMillis()

			for (block in detectedBlocks) {
				if (!detectedBlocks.contains(block.getRelative(x, y, z))) {
					if (!block.getRelative(x, y, z).type.isAir) {
						owner.sendMessage("Obstructed at " + block.x + ", " + block.y + ", " + block.z  + " by " + block.type.toString())
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

    fun getCustomisedUndetectables(): MutableList<Material> {
		val customisedUndetectables = mutableListOf<Material>()

		defaultUndetectable.forEach {
			if (it.getBukkit() != null) customisedUndetectables.add(it.getBukkit()!!)
		}

		return customisedUndetectables
    }
}