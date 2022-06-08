package io.github.hydrazinemc.hydrazine.customitems

import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.plugin
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/**
 * The custom item type this ItemStack represents, if any
 */
val ItemStack.customItem: CustomItem?
	get() = CustomItems[
			this.itemMeta.persistentDataContainer.get(
				NamespacedKey(plugin, "custom_item_id"),
				PersistentDataType.STRING
			) ?: ""]

/**
 * The id (either CustomItem or Material) of the ItemStack
 * Note: Material IDs will be lowercase
 * @see [itemStackFromId]
 */
val ItemStack.id: String
	get() = this.customItem?.id ?: this.type.toString().lowercase()
