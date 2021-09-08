package io.github.petercrawley.minecraftstarshipplugin.ships

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class Interface: Listener {
	@EventHandler
	fun interfaceUse(event: PlayerInteractEvent) {
		if (event.hand != EquipmentSlot.HAND) return // PlayerInteractEvent is called twice for each hand.
		if (event.action != Action.RIGHT_CLICK_BLOCK) return // Ignore Air Punchers

		// We know that event.clickedBlock is not null at this point because of the action.
		if (event.clickedBlock!!.type != Material.JUKEBOX) return // Ignore blocks we don't care about.

		// Later we will do more then just attempt to detect a ship
		val ship = Starship(MSPLocation(event.clickedBlock!!.location), event.player)
		ship.detect()

		event.isCancelled = true
	}
}