package io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.control

import io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.Starship
import io.github.hydrazinemc.hydrazine.utils.Vector3
import io.github.hydrazinemc.hydrazine.utils.extensions.craft
import io.github.hydrazinemc.hydrazine.utils.extensions.hotbar
import io.github.hydrazinemc.hydrazine.utils.gui.hotbar.HotbarMenu
import io.github.hydrazinemc.hydrazine.utils.gui.namedItem
import io.github.hydrazinemc.hydrazine.utils.rotation.RotationAmount
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * Hotbar menu for basic ship control and movement functions
 */
object ShipControlHotbar : HotbarMenu() {
	override val toggleable = true

	private val menuEntries = mutableListOf(
		namedItem(Material.LIME_STAINED_GLASS, "<green>Increase Speed", null),
		namedItem(Material.ORANGE_STAINED_GLASS, "<gold>Decrease Speed", null),
		namedItem(Material.RED_STAINED_GLASS, "<red>Full Stop", null),
		null,
		namedItem(Material.BLUE_STAINED_GLASS, "<blue>Right", null), // these might be
		namedItem(Material.BLUE_STAINED_GLASS, "<blue>Left", null),
		null,
		null,
		namedItem(Material.RED_CONCRETE, "<red>Unpilot Ship", null)
	)

	override fun onMenuOpened(player: Player) {
		player.hotbar = menuEntries
	}

	override fun onButtonClicked(index: Int, player: Player) {
		val ship = player.craft as? Starship ?: run {
			player.sendRichMessage("<red>You are not piloting a starship, yet the ship menu is open! This is a bug!")
			return
		}
		when (index) {
			0 -> ship.velocity += Vector3(player.eyeLocation.direction.normalize())
			1 -> ship.velocity -= Vector3(player.eyeLocation.direction.normalize())
			2 -> ship.velocity = Vector3.zero
			4 -> ship.controlQueue.add { ship.queueRotation(RotationAmount.CLOCKWISE) }
			5 -> ship.controlQueue.add { ship.queueRotation(RotationAmount.COUNTERCLOCKWISE) }
			8 -> ship.controlQueue.add { if (ship.deactivateCraft()) closeMenu(player) }
		}
	}
}
