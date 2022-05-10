package io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.control

import io.github.hydrazinemc.hydrazine.crafts.pilotable.Pilotable
import io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.Starship
import io.github.hydrazinemc.hydrazine.utils.RotationAmount
import io.github.hydrazinemc.hydrazine.utils.Vector3
import io.github.hydrazinemc.hydrazine.utils.extensions.craft
import io.github.hydrazinemc.hydrazine.utils.extensions.hotbar
import io.github.hydrazinemc.hydrazine.utils.extensions.sendMiniMessage
import io.github.hydrazinemc.hydrazine.utils.gui.hotbar.HotbarMenu
import io.github.hydrazinemc.hydrazine.utils.namedItem
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * Hotbar menu for basic ship control and movement functions
 */
object ShipControlHotbar : HotbarMenu() {
	override val toggleable = true

	val menuEntries = mutableListOf(
		namedItem(Material.LIME_STAINED_GLASS, "<green>Increase Speed", null),
		namedItem(Material.ORANGE_STAINED_GLASS, "<red>Decrease Speed", null),
		null,
		null,
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
		val ship = player.craft as? Starship ?: run {
			player.sendMiniMessage("<red>You are not piloting a starship, yet the ship menu is open! This is a bug!")
			return
		}
		when (index) {
			0 -> ship.velocty += Vector3(player.location.direction.normalize())
			1 -> ship.velocty -= Vector3(player.location.direction.normalize())
			4 -> {
				ship.queueRotation(RotationAmount.CLOCKWISE)
			}
			5 -> {
				ship.queueRotation(RotationAmount.COUNTERCLOCKWISE)
			}
			8 -> {
				closeMenu(player)
				ship.deactivateCraft()
			}
		}
	}
}