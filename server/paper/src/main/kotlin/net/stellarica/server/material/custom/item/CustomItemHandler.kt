package net.stellarica.server.material.custom.item

import net.stellarica.server.material.type.item.CustomItemType
import net.stellarica.server.material.type.item.ItemType
import net.stellarica.server.utils.Tasks
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.enchantment.PrepareItemEnchantEvent
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.player.PlayerItemBreakEvent

object CustomItemHandler : Listener {
	/**
	 * Handles cancelling enchants for custom items, allows only those in [CustomItem.allowedEnchants]
	 * @see [onAnvilEnchant]
	 */
	@EventHandler
	fun onTableEnchant(event: PrepareItemEnchantEvent) {
		val item = ItemType.of(event.item) as? CustomItemType ?: return
		for (i in 0..2) {
			val offer = event.offers[i] ?: continue
			if (offer.enchantment !in (item.item.allowedEnchants ?: mutableSetOf())) event.offers[i] = null
		}
	}

	/**
	 * Handles cancelling enchants for custom items, allows only those in [CustomItem.allowedEnchants]
	 * @see [onTableEnchant]
	 */
	@EventHandler
	fun onAnvilEnchant(event: PrepareAnvilEvent) {
		val item = event.result?.let { ItemType.of(it) } as? CustomItemType ?: return
		for (enchant in event.result!!.enchantments.keys) {
			if (enchant !in (item.item.allowedEnchants ?: mutableSetOf())) event.result = null
			return
		}
	}

	@EventHandler
	fun onCustomItemPlace(event: BlockPlaceEvent) {
		// prevent custom items with a vanilla type that's a block from being placed
		val item = ItemType.of(event.itemInHand) as? CustomItemType ?: return
		if (item.getBlock() == null) event.isCancelled = true
	}

	@EventHandler
	fun onPowerableItemBreak(event: PlayerItemBreakEvent) {
		if (!event.brokenItem.isPowerable) return
		// https://bukkit.org/threads/playeritembreakevent-cancelling.282678/
		event.brokenItem.amount += 1 // If there's a custom item dupe it's probably because of this
		Tasks.syncDelay(1) { event.brokenItem.power = 0 /* updates durability bar */ }
	}
}
