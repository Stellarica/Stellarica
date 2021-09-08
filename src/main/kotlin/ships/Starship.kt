package io.github.petercrawley.minecraftstarshipplugin.ships

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin
import org.bukkit.Location
import org.bukkit.entity.Player
import ships.DetectionTask

class Starship(val origin: Location, val player: Player) {
	val detectedBlocks: MutableSet<Location> = mutableSetOf()

	fun detect() {
		DetectionTask(this).runTaskAsynchronously(MinecraftStarshipPlugin.getPlugin())
	}
}