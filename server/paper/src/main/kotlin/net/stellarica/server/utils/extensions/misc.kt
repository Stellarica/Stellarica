package net.stellarica.server.utils.extensions

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.stellarica.server.customblocks.CustomBlocks
import net.stellarica.server.customitems.CustomItem
import net.stellarica.server.customitems.CustomItems
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.block.data.MultipleFacing
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/**
 * This string as a Component, using MiniMessage formatting
 */
val String.asMiniMessage: Component get() = MiniMessage.miniMessage().deserialize(this.trimIndent())


fun Audience.sendRichMessage(message: String) = this.sendMessage(message.asMiniMessage)

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
			NamespacedKey(net.stellarica.server.StellaricaServer.plugin, "custom_item_id"),
			PersistentDataType.STRING
		) ?: ""]

/**
 * The id (either CustomItem or Material) of the ItemStack
 * Note: Material IDs will be lowercase
 * @see [itemStackFromId]
 */
val ItemStack.id: String
	get() = this.customItem?.id ?: this.type.toString().lowercase()
