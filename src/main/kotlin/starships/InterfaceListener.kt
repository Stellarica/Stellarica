package io.github.petercrawley.minecraftstarshipplugin.starships

import io.github.petercrawley.minecraftstarshipplugin.customblocks.MSPMaterial
import io.github.petercrawley.minecraftstarshipplugin.starships.screens.InterfaceScreen
import io.github.petercrawley.minecraftstarshipplugin.utils.BlockLocation
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class InterfaceListener : Listener {
	@EventHandler
	fun onPlayerInteractEvent(event: PlayerInteractEvent) {
		if (event.hand == EquipmentSlot.HAND && event.action == Action.RIGHT_CLICK_BLOCK && !event.player.isSneaking) {
			if (MSPMaterial(event.clickedBlock!!) == MSPMaterial("INTERFACE")) {
				InterfaceScreen(
					event.player,
					Starship(BlockLocation(event.clickedBlock!!), event.player.world, event.player)
				)

				event.isCancelled = true
			}
		}
	}
}