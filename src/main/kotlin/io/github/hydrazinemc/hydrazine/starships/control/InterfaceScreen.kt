package io.github.hydrazinemc.hydrazine.starships.control

import io.github.hydrazinemc.hydrazine.starships.Starship
import io.github.hydrazinemc.hydrazine.utils.gui.Screen
import io.github.hydrazinemc.hydrazine.utils.extensions.isPilotingShip
import io.github.hydrazinemc.hydrazine.utils.namedItem
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType

class InterfaceScreen(player: Player, private val starship: Starship) : Screen() {
	init {
		createScreen(player, InventoryType.HOPPER, "Starship Interface")

		screen.setItem(0, namedItem(Material.MINECART, "Detect Starship", null))
		if (player.isPilotingShip) screen.setItem(1, namedItem(Material.COMPASS, "Unpilot Starship", null))
		else screen.setItem(1, namedItem(Material.COMPASS, "Pilot Starship", null))
		screen.setItem(4, namedItem(Material.BEDROCK, "Allow Undetectables", null))
	}

	override fun onScreenButtonClicked(slot: Int) {
		when (slot) {
			0 -> starship.detectStarship(player)
			1 -> {
				if (player.isPilotingShip) starship.deactivateStarship()
				else {
					starship.activateStarship(player)
					ShipControlHotbar.openMenu(player)
				}
				closeScreen()
			}
			4 -> {
				AllowUndetectablesScreen(player, starship)
				closeScreen()
			}
		}
	}
}