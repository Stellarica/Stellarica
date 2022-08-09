package io.github.hydrazinemc.hydrazine.utils.extensions

import io.github.hydrazinemc.hydrazine.Hydrazine
import io.github.hydrazinemc.hydrazine.customblocks.CustomBlocks
import io.github.hydrazinemc.hydrazine.customitems.CustomItem
import io.github.hydrazinemc.hydrazine.customitems.CustomItems
import io.github.hydrazinemc.hydrazine.utils.locations.BlockLocation
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.block.data.MultipleFacing
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/**
 * This string as a Component, using MiniMessage formatting
 */
val String.asMiniMessage: Component get() = MiniMessage.miniMessage().deserialize(this.trimIndent())

/**
 * The location of this block
 */
val Block.blockLocation: BlockLocation get() = BlockLocation(this.location)

/**
 * The id (either CustomBlock or Material) of the block
 * Note: Material IDs will be lowercase
 */
val Block.id: String
	get() = (
		CustomBlocks[this.blockData as? MultipleFacing] ?: run {
			return this.type.toString().lowercase()
		}).id.lowercase()

/**
 * The custom item type this ItemStack represents, if any
 */
val ItemStack.customItem: CustomItem?
	get() = CustomItems[
			this.itemMeta.persistentDataContainer.get(
				NamespacedKey(Hydrazine.plugin, "custom_item_id"),
				PersistentDataType.STRING
			) ?: ""]

/**
 * The id (either CustomItem or Material) of the ItemStack
 * Note: Material IDs will be lowercase
 * @see [itemStackFromId]
 */
val ItemStack.id: String
	get() = this.customItem?.id ?: this.type.toString().lowercase()
