package net.stellarica.server.material.custom.item

import net.minecraft.resources.ResourceLocation
import net.stellarica.server.material.custom.item.CustomItemHandler.items
import net.stellarica.server.material.type.item.CustomItemType
import net.stellarica.server.material.type.item.ItemType
import org.bukkit.event.EventHandler
import org.bukkit.event.enchantment.PrepareItemEnchantEvent
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.Listener

/**
 * Keeps track of registed [items]
 * Utilities for dealing with custom items
 */
object CustomItemHandler: Listener {
	private val items = mutableMapOf<ResourceLocation, CustomItem>()

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
		event.result!!.enchantments.keys.forEach {
			if (it !in (item.item.allowedEnchants ?: mutableSetOf())) event.result = null
			return
		}
	}
}
