package io.github.hydrazinemc.hydrazine.starships.control

import io.github.hydrazinemc.hydrazine.utils.RotationAmount
import io.github.hydrazinemc.hydrazine.utils.extensions.hotbar
import io.github.hydrazinemc.hydrazine.utils.extensions.starship
import io.github.hydrazinemc.hydrazine.utils.gui.hotbar.HotbarMenu
import io.github.hydrazinemc.hydrazine.utils.namedItem
import org.bukkit.Material
import org.bukkit.entity.Player

object ShipControlHotbar: HotbarMenu() {
	override val toggleable = true

	val menuEntries = mutableListOf(
		namedItem(Material.LIME_STAINED_GLASS, "<green>Increase Speed", null),
		namedItem(Material.ORANGE_STAINED_GLASS, "<red>Decrease Speed", null),
		namedItem(Material.GREEN_STAINED_GLASS_PANE, "<green>Full Speed", null),
		namedItem(Material.RED_STAINED_GLASS_PANE, "<red>Full Stop", null),
		namedItem(Material.BLUE_STAINED_GLASS, "<blue>Left", null),
		namedItem(Material.BLUE_STAINED_GLASS, "<blue>Right", null),
		null,
		null,
		namedItem(Material.RED_CONCRETE, "<red>Unpilot Ship", null)
	)

	override fun onMenuOpened(player: Player) {
		player.hotbar = menuEntries
	}

	override fun onButtonClicked(index: Int, player: Player) {
		val ship = player.starship ?: return // warn here?
		when (index) {
			4 -> {
				ship.queueRotation(RotationAmount.COUNTERCLOCKWISE)
			}
			5 -> {
				ship.queueRotation(RotationAmount.CLOCKWISE)
			}
			8 -> {
				closeMenu(player)
				ship.deactivateStarship()
			}
		}
	}
}