package net.stellarica.server.crafts.starships.control

import net.stellarica.server.crafts.starships.Starship
import net.stellarica.server.utils.extensions.isPilotingCraft
import net.stellarica.server.utils.gui.Screen
import net.stellarica.server.utils.gui.namedItem
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType

/**
 * The GUI opened when a player clicks the starship interface block
 */
class StarshipInterfaceScreen(player: Player, private val craft: Starship) : Screen() {
	init {
		createScreen(player, InventoryType.HOPPER, "Starship Interface")
		setAll(
			0 to namedItem(Material.MINECART, "Detect Starship", null),
			1 to if (!player.isPilotingCraft) {
				namedItem(Material.COMPASS, "Pilot Starship")
			} else {
				namedItem(Material.COMPASS, "Unpilot Starship")
			}
		)
	}

	override fun onScreenButtonClicked(slot: Int) {
		if (craft.owner == null) craft.owner = player
		when (slot) {
			0 -> craft.detect()
			1 -> {
				if (player.isPilotingCraft) craft.deactivateCraft()
				else craft.activateCraft(player)
				closeScreen()
			}
		}
	}
}
