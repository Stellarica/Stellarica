package io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.listeners

import io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.Starship
import io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.control.StarshipInterfaceScreen
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

/**
 *  Listener for the starship interface
 *  @see StarshipInterfaceScreen
 */
class InterfaceListener : Listener {
	/**
	 * Opens the starships interface Screen when a jukebox is clicked on
	 */
	@EventHandler
	fun onPlayerInteractEvent(event: PlayerInteractEvent) {
		if (event.hand == EquipmentSlot.HAND && event.action == Action.RIGHT_CLICK_BLOCK && !event.player.isSneaking) {
			if (event.clickedBlock!!.type == Material.JUKEBOX) {
				StarshipInterfaceScreen(
					event.player,
					Starship(event.clickedBlock!!.location)
				)

				event.isCancelled = true
			}
		}
	}
}
