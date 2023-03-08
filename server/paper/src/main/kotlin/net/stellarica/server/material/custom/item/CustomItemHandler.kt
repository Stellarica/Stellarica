package net.stellarica.server.material.custom.item

import net.minecraft.resources.ResourceLocation
import net.stellarica.server.StellaricaServer.Companion.klogger
import net.stellarica.server.StellaricaServer.Companion.plugin
import net.stellarica.server.material.custom.item.CustomItemHandler.items
import net.stellarica.server.utils.extensions.customItem
import net.stellarica.server.utils.extensions.id
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.enchantment.PrepareItemEnchantEvent
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import java.net.http.WebSocket.Listener

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
