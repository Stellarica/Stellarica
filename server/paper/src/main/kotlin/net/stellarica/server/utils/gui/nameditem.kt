package net.stellarica.server.utils.gui

import net.kyori.adventure.text.Component
import net.stellarica.server.utils.extensions.asMiniMessage
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * MiniMessage formatting is allowed in both lore and name
 * @param name the name of the item
 * @param lore the lore of the item
 */
fun namedItem(material: Material, name: String, lore: MutableList<String>?): ItemStack {
	val stack = ItemStack(material)
	val meta = stack.itemMeta
	meta.displayName(name.asMiniMessage)
	// Someone code golf this
	val newLore = mutableListOf<Component>()
	lore?.forEach {
		newLore.add(it.asMiniMessage)
	}
	meta.lore(newLore)
	stack.itemMeta = meta
	return stack
}
