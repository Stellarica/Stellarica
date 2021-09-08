package io.github.petercrawley.minecraftstarshipplugin.ships

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import ships.DetectionTask

class Starship(val origin: Location, val player: Player, private val plugin: JavaPlugin) {
	val detectedBlocks: MutableSet<Location> = mutableSetOf()

	fun detect() {
		DetectionTask(this).runTaskAsynchronously(plugin)
	}
}