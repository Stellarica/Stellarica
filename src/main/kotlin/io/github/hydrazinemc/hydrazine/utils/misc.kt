package io.github.hydrazinemc.hydrazine.utils

import io.github.hydrazinemc.hydrazine.utils.extensions.asMiniMessage
import net.kyori.adventure.text.Component
import org.bukkit.Axis
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

/**
 * I have 0 idea what this does or why this is here.
 * Blame Peter.
 */
fun bitOfByte(byte: Byte, bit: Int): Boolean {
	return ((byte.toInt() shr bit) and 1) == 1
}

enum class RotationAmount(val asRadians: Double = 0.0, val asDegrees: Float = 0f) {
	CLOCKWISE(-Math.PI / 2, -90f),
	COUNTERCLOCKWISE(Math.PI / 2, 90f),
	REVERSE(Math.PI, 180f),
	NONE,
}

fun rotateAxis(axis: Axis): Axis = when (axis) {
	Axis.X -> Axis.Z
	Axis.Z -> Axis.X
	Axis.Y -> Axis.Y
}