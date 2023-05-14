package net.stellarica.server.craft.starship

import net.minecraft.core.Direction
import net.stellarica.server.craft.starship.control.StarshipInterfaceScreen
import net.stellarica.server.material.custom.block.type.MiscCustomBlocks
import net.stellarica.server.material.type.block.BlockType
import net.stellarica.server.util.extension.toBlockPos
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld
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
			if (BlockType.of(event.clickedBlock!!) == BlockType.of(MiscCustomBlocks.COMPUTER_CORE)) {
				if (!event.player.hasPermission("stellarica.starship")) return
				StarshipInterfaceScreen(
						event.player,
						Starship(
								event.clickedBlock!!.toBlockPos(),
								Direction.NORTH,
								(event.player.world as CraftWorld).handle,
								event.player
						)
				)

				event.isCancelled = true
			}
		}
	}
}
