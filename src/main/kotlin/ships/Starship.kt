package io.github.petercrawley.minecraftstarshipplugin.ships

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player

/*
	I am aware that this async task is not properly being shutdown when the plugin is reloaded.
	I will fix it later.

	TODO: Fix.
 */

class Starship(private val origin: MSPLocation, private val owner: Player) {
	// TODO: This should be loaded from a config file.
	private val nonDetectableBlocks: Set<Material> = setOf(
		Material.AIR
	)

	private val detectedBlocks: MutableSet<MSPLocation> = mutableSetOf()
	private val checkedBlocks:  MutableSet<MSPLocation> = mutableSetOf()
	private val blocksToCheck:  MutableSet<MSPLocation> = mutableSetOf()

	fun detect() {
		Bukkit.getScheduler().runTaskAsynchronously(MinecraftStarshipPlugin.getPlugin(), Runnable {
			owner.sendMessage("Detecting Starship, this can take a few seconds.")

			val startTime: Long = System.currentTimeMillis()

			blocksToCheck.add(origin)

			while (blocksToCheck.isNotEmpty()) {
				if (detectedBlocks.size == 1000000) {
					owner.sendMessage("Reached arbitrary detection limit. (1,000,000)")
					break
				}

				val currentBlock = blocksToCheck.first()
				blocksToCheck.remove(currentBlock)

				if (checkedBlocks.contains(currentBlock)) continue

				checkedBlocks.add(currentBlock)

				if (nonDetectableBlocks.contains(currentBlock.bukkit().block.type)) continue

				detectedBlocks.add(currentBlock)

				blocksToCheck.add(currentBlock.add( 1, 0, 0))
				blocksToCheck.add(currentBlock.add(-1, 0, 0))
				blocksToCheck.add(currentBlock.add( 0, 1, 0))
				blocksToCheck.add(currentBlock.add( 0,-1, 0))
				blocksToCheck.add(currentBlock.add( 0, 0, 1))
				blocksToCheck.add(currentBlock.add( 0, 0,-1))
			}

			val endTime: Long = System.currentTimeMillis()

			owner.sendMessage("Detected " + detectedBlocks.size + " blocks. Took " + (endTime - startTime) + "ms")
		})
	}
}