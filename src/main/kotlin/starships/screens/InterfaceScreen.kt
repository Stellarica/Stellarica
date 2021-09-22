package io.github.petercrawley.minecraftstarshipplugin.starships.screens

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.itemWithName
import io.github.petercrawley.minecraftstarshipplugin.starships.Screen
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType

class InterfaceScreen(player: Player, starship: Any?): Screen() {
	init {
		createScreen(player, InventoryType.HOPPER, "Starship Interface")

		screen.setItem(0, itemWithName(Material.MINECART, "Detect Starship"    , 128, 255, 128, bold = true))
		screen.setItem(1, itemWithName(Material.COMPASS , "Pilot Starship"     , 128, 128, 255, bold = true))
		screen.setItem(4, itemWithName(Material.BEDROCK , "Allow Undetectables", 255, 128, 128, bold = true))
	}

	override fun onScreenButtonClicked(slot: Int) {
		when (slot) {
			1, 4 -> {
				closeScreen()
			}
		}
	}
}