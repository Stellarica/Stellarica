package io.github.petercrawley.minecraftstarshipplugin.ships

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player


/*
	I am aware that this async task is not properly being shutdown when the plugin is reloaded.
	I will fix it later.

	TODO: Fix.
 */

class Starship(private val origin: Location, private val player: Player) {
	// TODO: This should be loaded from a config file.
	private val nonDetectableBlocks: Set<Material> = setOf(
		Material.AIR
	)

	private val detectedBlocks: MutableSet<MSPBlockLocation> = mutableSetOf()
	private val checkedBlocks: MutableSet<MSPBlockLocation> = mutableSetOf()
	private val blocksToCheck: MutableSet<MSPBlockLocation> = mutableSetOf()

	fun detect() {
		Bukkit.getScheduler().runTaskAsynchronously(MinecraftStarshipPlugin.getPlugin(), Runnable {
			player.sendMessage("Detecting Starship, this can take a few seconds.")

			val startTime: Long = System.currentTimeMillis()

			val world: World = origin.world

			blocksToCheck.add(MSPBlockLocation(origin))

			while (blocksToCheck.isNotEmpty()) {
				if (detectedBlocks.size == 1000000) {
					player.sendMessage("Reached arbitrary detection limit. (1,000,000)")
					break
				}

				val currentBlock = blocksToCheck.first()
				blocksToCheck.remove(currentBlock)

				if (checkedBlocks.contains(currentBlock)) continue

				checkedBlocks.add(currentBlock)

				if (nonDetectableBlocks.contains(currentBlock.bukkit(world).block.type)) continue

				detectedBlocks.add(currentBlock)

				blocksToCheck.add(currentBlock.add( 1, 0, 0))
				blocksToCheck.add(currentBlock.add(-1, 0, 0))
				blocksToCheck.add(currentBlock.add( 0, 1, 0))
				blocksToCheck.add(currentBlock.add( 0,-1, 0))
				blocksToCheck.add(currentBlock.add( 0, 0, 1))
				blocksToCheck.add(currentBlock.add( 0, 0,-1))
			}

			val endTime: Long = System.currentTimeMillis()

			player.sendMessage("Detected " + detectedBlocks.size + " blocks. Took " + (endTime - startTime) + "ms")
		})
	}
}