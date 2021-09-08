package io.github.petercrawley.minecraftstarshipplugin.ships

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import ships.DetectionTask

class Starship(val origin: Location, val player: Player) {
	val detectedBlocks: MutableSet<Location> = mutableSetOf()

	fun detect() {
		DetectionTask(this).runTaskAsynchronously(Bukkit.getPluginManager().getPlugin("MinecraftStarshipPlugin")!!)
	}
}