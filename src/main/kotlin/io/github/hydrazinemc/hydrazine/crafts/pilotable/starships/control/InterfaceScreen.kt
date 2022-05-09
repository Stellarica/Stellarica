package io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.control

import io.github.hydrazinemc.hydrazine.crafts.pilotable.Pilotable
import io.github.hydrazinemc.hydrazine.utils.extensions.isPilotingCraft
import io.github.hydrazinemc.hydrazine.utils.gui.Screen
import io.github.hydrazinemc.hydrazine.utils.namedItem
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType

class InterfaceScreen(player: Player, private val craft: Pilotable) : Screen() {
	init {
		createScreen(player, InventoryType.HOPPER, "Starship Interface")

		screen.setItem(0, namedItem(Material.MINECART, "Detect Starship", null))
		if (player.isPilotingCraft) screen.setItem(1, namedItem(Material.COMPASS, "Unpilot Starship", null))
		else screen.setItem(1, namedItem(Material.COMPASS, "Pilot Starship", null))
		screen.setItem(4, namedItem(Material.BEDROCK, "Allow Undetectables", null))
	}

	override fun onScreenButtonClicked(slot: Int) {
		if (craft.owner == null) craft.owner = player
		when (slot) {
			0 -> craft.detectCraft()
			1 -> {
				if (player.isPilotingCraft) craft.deactivateCraft()
				else {
					craft.activateCraft(player)
					ShipControlHotbar.openMenu(player)
				}
				closeScreen()
			}
			4 -> {
				AllowUndetectablesScreen(player, craft)
				closeScreen()
			}
		}
	}
}