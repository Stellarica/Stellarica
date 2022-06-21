package io.github.hydrazinemc.hydrazine.customitems.listeners

import io.github.hydrazinemc.hydrazine.customitems.customItem
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.PrepareItemEnchantEvent
import org.bukkit.event.inventory.PrepareAnvilEvent

/**
 * Disables enchanting of custom items excluding enchantments in [CustomItem.allowedEnchants]
 */
class ItemEnchantListener : Listener {
	/**
	 * Handles cancelling enchants for custom items, allows only those in [CustomItem.allowedEnchants]
	 * @see [onAnvilEnchant]
	 */
	@EventHandler
	fun onTableEnchant(event: PrepareItemEnchantEvent) {
		val item = event.item.customItem ?: return
		for (i in 0..2) {
			val offer = event.offers.get(i) ?: continue
			if (offer.enchantment !in item.allowedEnchants) event.offers.set(i, null)
		}
	}

	/**
	 * Handles cancelling enchants for custom items, allows only those in [CustomItem.allowedEnchants]
	 * @see [onTableEnchant]
	 */
	@EventHandler
	fun onAnvilEnchant(event: PrepareAnvilEvent) {
		val item = event.result?.customItem ?: return
		event.result!!.enchantments.keys.forEach {
			if (it !in item.allowedEnchants) event.result = null
			return
		}
	}
}
