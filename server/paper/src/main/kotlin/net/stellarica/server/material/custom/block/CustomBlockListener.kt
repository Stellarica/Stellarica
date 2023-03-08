package net.stellarica.server.material.custom.block

import net.stellarica.server.utils.extensions.customItem
import org.bukkit.block.data.MultipleFacing
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent

/**
 * Handles the placing and breaking of custom blocks
 */
class CustomBlockListener : Listener {

	/**
	 * Handles the placing of custom blocks
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	fun onCustomBlockPlace(event: BlockPlaceEvent) {
		val item = event.itemInHand.customItem ?: return
		val block = CustomBlocks[item] ?: return
		event.blockPlaced.blockData = block.blockData
	}

	/**
	 * Handles the breaking of custom blocks
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	fun onCustomBlockBreak(event: BlockBreakEvent) {
		// todo: don't drop if in creative mode
		val block = CustomBlocks[event.block.blockData as? MultipleFacing] ?: return
		if (!event.isDropItems) return
		event.isDropItems = false // no vanilla drops
		block.drops?.clone()?.let { event.block.location.world.dropItem(event.block.location, it) }
	}
}
