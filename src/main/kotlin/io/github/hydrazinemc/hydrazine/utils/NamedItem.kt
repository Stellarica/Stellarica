package io.github.hydrazinemc.hydrazine.utils

import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.Component.translatable
import net.kyori.adventure.text.format.Style.style
import net.kyori.adventure.text.format.TextColor.color
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class NamedItem(
	material: Material,
	name: String,
	translatable: Boolean,
	colorR: Int = 255,
	colorG: Int = 255,
	colorB: Int = 255,
	bold: Boolean = false,
	italic: Boolean = false
) : ItemStack(material) {
	init {
		var text = if (translatable) translatable(name) else text(name)

		var style = style(color(colorR, colorG, colorB))
		style = style.decoration(TextDecoration.ITALIC, italic)
		style = style.decoration(TextDecoration.BOLD, bold)

		text = text.style(style)

		val meta = itemMeta
		meta.displayName(text)

		itemMeta = meta
	}
}