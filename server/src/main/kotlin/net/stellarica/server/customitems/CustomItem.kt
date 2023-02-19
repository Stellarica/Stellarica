package net.stellarica.server.customitems

import net.kyori.adventure.text.Component
import net.stellarica.server.StellaricaServer.Companion.plugin
import net.stellarica.server.utils.extensions.asMiniMessage
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/**
 * Holds data for a custom item
 */
data class CustomItem(
	/**
	 * The id of the item, must be unique to this item
	 */
	val id: String,

	/**
	 * The display name of the item, MiniMessage formatting is allowed
	 */
	val name: String,

	/**
	 * The item's lore, MiniMessage formatting is allowed
	 */
	val lore: List<String>,

	/**
	 * The vanilla material behind this item
	 */
	val base: Material,

	/**
	 * Custom model data for this item
	 */
	val modelData: Int,

	/**
	 * The maximum power this item can hold.
	 * -1 if this item cannot hold power
	 */
	val maxPower: Int = -1,

	/**
	 * The allowed enchants for this item
	 */
	val allowedEnchants: MutableSet<Enchantment> = mutableSetOf()

) {

	/**
	 * Whether this item can hold power,
	 * @see maxPower
	 */
	val isPowerable: Boolean = maxPower > 0

	/**
	 * @return an [ItemStack] with [count] of this item
	 */
	fun getItem(count: Int = 1): ItemStack {
		val stack = ItemStack(base, count)
		stack.editMeta { meta ->
			meta.displayName(name.asMiniMessage)
			val loreComponents = mutableListOf<Component>() // this can be code golfed
			lore.forEach { loreComponents.add(it.asMiniMessage) }
			if (maxPower > 0) {
				loreComponents.add("<gray>Power: 0/$maxPower".asMiniMessage)
				meta.persistentDataContainer.set(
					NamespacedKey(plugin, "power"),
					PersistentDataType.INTEGER,
					0
				)
			}
			meta.lore(loreComponents)
			meta.setCustomModelData(modelData)
			meta.persistentDataContainer.set(
				NamespacedKey(plugin, "custom_item_id"),
				PersistentDataType.STRING,
				id
			)
		}
		return stack
	}
}
