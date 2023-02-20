package net.stellarica.server.crafts.pilotables.starships.listeners

import net.minecraft.core.Direction
import net.stellarica.server.crafts.pilotables.starships.Starship
import net.stellarica.server.crafts.pilotables.starships.control.StarshipInterfaceScreen
import net.stellarica.server.utils.extensions.BlockPos
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld
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
					Starship(event.clickedBlock!!.BlockPos, Direction.NORTH, (event.player.world as CraftWorld).handle)
				)

				event.isCancelled = true
			}
		}
	}
}
