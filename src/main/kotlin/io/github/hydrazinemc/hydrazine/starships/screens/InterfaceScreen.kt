package io.github.hydrazinemc.hydrazine.starships.screens

import io.github.hydrazinemc.hydrazine.starships.Starship
import io.github.hydrazinemc.hydrazine.utils.NamedItem
import io.github.hydrazinemc.hydrazine.utils.Screen
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType

class InterfaceScreen(player: Player, private val starship: Starship) : Screen() {
	init {
		createScreen(player, InventoryType.HOPPER, "Starship Interface")

		screen.setItem(0, NamedItem(Material.MINECART, "Detect Starship", false, 128, 255, 128, bold = true))
		screen.setItem(1, NamedItem(Material.COMPASS, "Pilot Starship", false, 128, 128, 255, bold = true))
		screen.setItem(4, NamedItem(Material.BEDROCK, "Allow Undetectables", false, 255, 128, 128, bold = true))
	}

	override fun onScreenButtonClicked(slot: Int) {
		when (slot) {
			0 -> starship.detectStarship(player)
			1 -> {
				starship.activateStarship(player)

				// Not exactly sure where the best place for this is, as piloting doesn't seem to be quite done, but for now, here should work
				// val event = StarshipPilotEvent(starship, player)
				// Bukkit.getPluginManager().callEvent(event)

				closeScreen()
			}
			4 -> {
				AllowUndetectablesScreen(player, starship)
				closeScreen()
			}
		}
	}
}