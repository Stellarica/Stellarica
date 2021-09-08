package ships

import io.github.petercrawley.minecraftstarshipplugin.ships.Starship
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.scheduler.BukkitRunnable

class DetectionTask(private val starship: Starship): BukkitRunnable() {
	// TODO: This should be loaded from a config file.
	private val nonDetectableBlocks: Set<Material> = setOf(
		Material.AIR
	)

	override fun run() {
		starship.player.sendMessage("Detecting Starship, this can take a few seconds.")

		starship.detectedBlocks.clear()

		val startTime: Long = System.currentTimeMillis()

		val checkedBlocks: MutableSet<Location> = mutableSetOf()
		val blocksToCheck: MutableSet<Location> = mutableSetOf()

		blocksToCheck.add(starship.origin)

		while (blocksToCheck.isNotEmpty()) {
			if (starship.detectedBlocks.size == 1000000) {
				starship.player.sendMessage("Reached arbitrary detection limit. (1,000,000)")
				break
			}

			val currentBlock = blocksToCheck.first()
			blocksToCheck.remove(currentBlock)

			if (checkedBlocks.contains(currentBlock)) continue

			checkedBlocks.add(currentBlock)

			if (nonDetectableBlocks.contains(currentBlock.block.type)) continue // NOT THREAD SAFE

			starship.detectedBlocks.add(currentBlock)

			blocksToCheck.add(currentBlock.clone().add( 1.0, 0.0, 0.0))
			blocksToCheck.add(currentBlock.clone().add(-1.0, 0.0, 0.0))
			blocksToCheck.add(currentBlock.clone().add( 0.0, 1.0, 0.0))
			blocksToCheck.add(currentBlock.clone().add( 0.0,-1.0, 0.0))
			blocksToCheck.add(currentBlock.clone().add( 0.0, 0.0, 1.0))
			blocksToCheck.add(currentBlock.clone().add( 0.0, 0.0,-1.0))
		}

		val endTime: Long = System.currentTimeMillis()

		starship.player.sendMessage("Detected " + starship.detectedBlocks.size + " blocks. Took " + (endTime - startTime) + "ms")
	}
}