package net.stellarica.server.craft.starship

import net.stellarica.server.material.block.custom.MiscCustomBlocks
import net.stellarica.server.material.block.type.BlockType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class InterfaceListener : Listener {
	@EventHandler
	fun onPlayerInteractEvent(event: PlayerInteractEvent) {
		if (event.hand == EquipmentSlot.HAND && event.action == Action.RIGHT_CLICK_BLOCK && !event.player.isSneaking) {
			if (BlockType.of(event.clickedBlock!!) == BlockType.of(MiscCustomBlocks.COMPUTER_CORE)) {

				event.isCancelled = true
			}
		}
	}
}
