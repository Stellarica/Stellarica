package io.github.petercrawley.minecraftstarshipplugin

import org.bukkit.entity.Player

class Starship(private val player: Player) {
	fun detect() {
		player.sendMessage("Detecting")
	}
}