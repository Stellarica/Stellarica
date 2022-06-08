package io.github.hydrazinemc.hydrazine.customitems

import io.github.hydrazinemc.hydrazine.Hydrazine
import io.github.hydrazinemc.hydrazine.utils.extensions.asMiniMessage
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
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
	 * The vanilla material behind this item	 */
	val base: Material,

	/**
	 * Custom model data for this item
	 */
	val modelData: Int,
	) {

	/**
	 * @return an [ItemStack] with [count] of this item
	 */
	fun getItem(count: Int = 1): ItemStack {
		val stack = ItemStack(base, count)
		stack.editMeta { meta ->
			meta.displayName(name.asMiniMessage)
			val loreComponents = mutableListOf<Component>() // this can be code golfed
			lore.forEach { loreComponents.add(it.asMiniMessage) }
			meta.lore(loreComponents)
			meta.setCustomModelData(modelData)
			meta.persistentDataContainer.set(
				NamespacedKey(Hydrazine.plugin, "custom_item_id"),
				PersistentDataType.STRING,
				id
			)
		}
		return stack
	}
}
