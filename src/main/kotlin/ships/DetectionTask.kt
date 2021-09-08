package ships

import io.github.petercrawley.minecraftstarshipplugin.ships.MSPBlockLocation
import io.github.petercrawley.minecraftstarshipplugin.ships.Starship
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.scheduler.BukkitRunnable

/*

	I am aware that this async task is not properly being shutdown when the plugin is reloaded.
	I will fix it later.

	TODO: Fix.

 */


class DetectionTask(private val starship: Starship): BukkitRunnable() {
	// TODO: This should be loaded from a config file.
	private val nonDetectableBlocks: Set<Material> = setOf(
		Material.AIR
	)

	override fun run() {
		starship.player.sendMessage("Detecting Starship, this can take a few seconds.")

		val detectedBlocks: MutableSet<MSPBlockLocation> = mutableSetOf()

		val startTime: Long = System.currentTimeMillis()

		val checkedBlocks: MutableSet<MSPBlockLocation> = mutableSetOf()
		val blocksToCheck: MutableSet<MSPBlockLocation> = mutableSetOf()

		val world: World = starship.origin.world

		blocksToCheck.add(MSPBlockLocation(starship.origin))

		while (blocksToCheck.isNotEmpty()) {
			if (detectedBlocks.size == 1000000) {
				starship.player.sendMessage("Reached arbitrary detection limit. (1,000,000)")
				break
			}

			val currentBlock = blocksToCheck.first()
			blocksToCheck.remove(currentBlock)

			if (checkedBlocks.contains(currentBlock)) continue

			checkedBlocks.add(currentBlock)

			if (nonDetectableBlocks.contains(currentBlock.bukkit(world).block.type)) continue // NOT THREAD SAFE

			detectedBlocks.add(currentBlock)

			blocksToCheck.add(currentBlock.add( 1, 0, 0))
			blocksToCheck.add(currentBlock.add(-1, 0, 0))
			blocksToCheck.add(currentBlock.add( 0, 1, 0))
			blocksToCheck.add(currentBlock.add( 0,-1, 0))
			blocksToCheck.add(currentBlock.add( 0, 0, 1))
			blocksToCheck.add(currentBlock.add( 0, 0,-1))
		}

		val endTime: Long = System.currentTimeMillis()

		starship.player.sendMessage("Detected " + detectedBlocks.size + " blocks. Took " + (endTime - startTime) + "ms")
	}
}