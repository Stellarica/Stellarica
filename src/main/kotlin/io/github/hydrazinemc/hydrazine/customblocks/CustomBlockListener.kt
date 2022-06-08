package io.github.hydrazinemc.hydrazine.customblocks

import io.github.hydrazinemc.hydrazine.customitems.customItem
import io.github.hydrazinemc.hydrazine.events.HydrazineConfigReloadEvent
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
		val block = CustomBlocks[event.block.blockData as? MultipleFacing] ?: return
		if (!event.isDropItems) return
		event.isDropItems = false // no vanilla drops
		block.drops?.clone()?.let { event.block.location.world.dropItem(event.block.location, it) }
	}

	/**
	 * Reloads custom block configuration on [HydrazineConfigReloadEvent]
	 * @see CustomBlocks.loadFromConfig
	 */
	@EventHandler
	fun onMSPConfigReload(event: HydrazineConfigReloadEvent) {
		CustomBlocks.loadFromConfig()
	}
}
