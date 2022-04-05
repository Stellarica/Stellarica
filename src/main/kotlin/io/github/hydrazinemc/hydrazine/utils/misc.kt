package io.github.hydrazinemc.hydrazine.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack

fun CommandSender.sendMiniMessage(message: String) = sendMessage(message.asMiniMessage)
val String.asMiniMessage: Component get() = miniMessage().deserialize(this.trimIndent())

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