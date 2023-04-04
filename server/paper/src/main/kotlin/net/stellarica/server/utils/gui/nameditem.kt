package net.stellarica.server.utils.gui

import net.stellarica.server.utils.extensions.asMiniMessage
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * MiniMessage formatting is allowed in both lore and name
 * @param name the name of the item
 * @param lore the lore of the item
 */
fun namedItem(material: Material, name: String, lore: MutableList<String>?): ItemStack {
	return ItemStack(material).also{ it.editMeta { meta ->
		meta.displayName(name.asMiniMessage)
		meta.lore(lore?.map { it.asMiniMessage})
	}}
}
