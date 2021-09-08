package io.github.petercrawley.minecraftstarshipplugin

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player

class Starship(private val origin: Location, private val player: Player) {
	private val detectedBlocks: MutableSet<Location> = mutableSetOf()

	// TODO: This should be loaded from a config file.
	private val nonDetectableBlocks: Set<Material> = setOf(
		Material.AIR
	)

	fun detect() {
		player.sendMessage("Detecting Starship, this can take a few seconds.")

		detectedBlocks.clear()

		val startTime: Long = System.currentTimeMillis()
		val maxTime: Long = startTime + 5000

		val checkedBlocks: MutableSet<Location> = mutableSetOf()
		val blocksToCheck: MutableSet<Location> = mutableSetOf()

		blocksToCheck.add(origin)

		while (blocksToCheck.isNotEmpty()) {
			if (System.currentTimeMillis() > maxTime) {
				detectedBlocks.clear()
				player.sendMessage("Ship took too long to detect (5 seconds). Reduce block count or try again.")
				return
			}

			val currentBlock = blocksToCheck.first()
			blocksToCheck.remove(currentBlock)

			if (checkedBlocks.contains(currentBlock)) continue

			checkedBlocks.add(currentBlock)

			if (nonDetectableBlocks.contains(currentBlock.block.type)) continue

			detectedBlocks.add(currentBlock)

			blocksToCheck.add(currentBlock.clone().add( 1.0, 0.0, 0.0))
			blocksToCheck.add(currentBlock.clone().add(-1.0, 0.0, 0.0))
			blocksToCheck.add(currentBlock.clone().add( 0.0, 1.0, 0.0))
			blocksToCheck.add(currentBlock.clone().add( 0.0,-1.0, 0.0))
			blocksToCheck.add(currentBlock.clone().add( 0.0, 0.0, 1.0))
			blocksToCheck.add(currentBlock.clone().add( 0.0, 0.0,-1.0))
		}

		val endTime: Long = System.currentTimeMillis()

		player.sendMessage("Detected " + detectedBlocks.size + " blocks. Took " + (endTime - startTime) + "ms")
	}
}