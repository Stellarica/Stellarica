package io.github.hydrazinemc.hydrazine.starships.listeners

import io.github.hydrazinemc.hydrazine.starships.Starship
import io.github.hydrazinemc.hydrazine.starships.screens.InterfaceScreen
import io.github.hydrazinemc.hydrazine.utils.BlockLocation
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class InterfaceListener : Listener {
	@EventHandler
	fun onPlayerInteractEvent(event: PlayerInteractEvent) {
		if (event.hand == EquipmentSlot.HAND && event.action == Action.RIGHT_CLICK_BLOCK && !event.player.isSneaking) {
			if (event.clickedBlock!!.type == Material.JUKEBOX) {
				InterfaceScreen(
					event.player,
					Starship(BlockLocation(event.clickedBlock!!), event.player.world)
				)

				event.isCancelled = true
			}
		}
	}
}