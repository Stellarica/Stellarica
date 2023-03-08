package net.stellarica.server.crafts.starships.control

import net.minecraft.world.level.block.Rotation
import net.stellarica.server.crafts.starships.Starship
import net.stellarica.server.utils.extensions.craft
import net.stellarica.server.utils.extensions.hotbar
import net.stellarica.server.utils.extensions.toBlockPos
import net.stellarica.server.utils.gui.hotbar.HotbarMenu
import net.stellarica.server.utils.gui.namedItem
import org.bukkit.Material
import org.bukkit.entity.Player

/**
 * Hotbar menu for basic ship control and movement functions
 */
object ShipControlHotbar : HotbarMenu() {
	override val toggleable = true

	private val menuEntries = mutableListOf(
		namedItem(Material.LIME_STAINED_GLASS, "<green>Precision Movement", null),
		null,
		null,
		null,
		namedItem(Material.BLUE_STAINED_GLASS, "<blue>Left", null), // these might be
		namedItem(Material.BLUE_STAINED_GLASS, "<blue>Right", null),
		null,
		namedItem(Material.FIRE_CHARGE, "<red>Fire", null),
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
			0 -> ship.move(
				player.eyeLocation.direction.normalize().multiply(1.5f).toLocation(player.world).toBlockPos()
			)

			1 -> TODO() // ship.velocity -= Vec3(player.eyeLocation.direction.normalize())
			2 -> TODO() // ship.velocity = Vec3.zero
			4 -> ship.rotate(Rotation.CLOCKWISE_90)
			5 -> ship.rotate(Rotation.COUNTERCLOCKWISE_90)
			7 -> ship.weapons.fire()
			8 -> ship.deactivateCraft()
		}
	}
}
