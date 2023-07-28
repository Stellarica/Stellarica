package net.stellarica.server.craft.starship.control

import net.stellarica.server.util.extension.craft
import net.stellarica.server.util.extension.hotbar
import net.stellarica.server.util.gui.hotbar.HotbarMenu
import net.stellarica.server.util.gui.namedItem
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * Hotbar menu for basic ship control and movement functions
 */
object ShipControlHotbar : HotbarMenu() {
	override val toggleable = true

	private val menuEntries = mutableListOf(
		namedItem(Material.LIME_STAINED_GLASS_PANE, "<green>Precision Movement", null),
		namedItem(Material.YELLOW_STAINED_GLASS_PANE, "<yellow>Accelerate", null),
		namedItem(Material.RED_STAINED_GLASS_PANE, "<red>Stop Ship", null),
		null,
		namedItem(Material.BLUE_STAINED_GLASS_PANE, "<blue>Left", null), // these might be
		namedItem(Material.BLUE_STAINED_GLASS_PANE, "<blue>Right", null),
		namedItem(Material.FIRE_CHARGE, "<red>Fire Heavy", null),
		namedItem(Material.FLINT_AND_STEEL, "<red>Fire Light", null),
		namedItem(Material.RED_CONCRETE, "<red>Unpilot Ship", null)
	)

	override fun onMenuOpened(player: Player) {
		player.hotbar = menuEntries
	}

	override fun onButtonClicked(index: Int, player: Player) {
		if ((player.hotbar[index]?.type?.let { player.getCooldown(it) } ?: 0) >= 1) return;

		val ship = player.craft as? Starship ?: run {
			player.sendRichMessage("<red>You are not piloting a starship, yet the ship menu is open! This is a bug!")
			return
		}
		when (index) {

		}
	}
}
