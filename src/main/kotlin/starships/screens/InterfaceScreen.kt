package io.github.petercrawley.minecraftstarshipplugin.starships.screens

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.itemWithName
import io.github.petercrawley.minecraftstarshipplugin.Screen
import io.github.petercrawley.minecraftstarshipplugin.starships.Starship
import io.github.petercrawley.minecraftstarshipplugin.starships.StarshipManager.activateStarship
import io.github.petercrawley.minecraftstarshipplugin.starships.StarshipManager.detectStarship
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType

class InterfaceScreen(private val starship: Starship, player: Player): Screen(player, InventoryType.HOPPER, "Starship Interface") {
	override fun init() {
		screen.setItem(0, itemWithName(Material.MINECART, "Detect Starship", 128, 255, 128, bold = true))
		screen.setItem(1, itemWithName(Material.COMPASS, "Pilot Starship", 128, 128, 255, bold = true))
		screen.setItem(4, itemWithName(Material.BEDROCK, "Allow Undetectables", 255, 128, 128, bold = true))
	}

	override fun update() {}

	override fun slotClicked(slot: Int) {
		when (slot) {
			0 -> detectStarship(starship)
			1 -> {
				activateStarship(starship)
				close()
			}
			4 -> {
				AllowUndetectableScreen(starship, player)
				close()
			}
		}
	}

	override fun closed() {}
}