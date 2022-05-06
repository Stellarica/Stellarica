package io.github.hydrazinemc.hydrazine.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import org.bukkit.Axis
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.Directional
import org.bukkit.block.data.Orientable
import org.bukkit.block.data.Rotatable
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import rotateBlockFace

/**
 * Alias for sendMessage(<message>.asMiniMessage)
 */
fun CommandSender.sendMiniMessage(message: String) = sendMessage(message.asMiniMessage)

/**
 * This string as a Component, using MiniMessage formatting
 */
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

/**
 * I have 0 idea what this does.
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

fun rotateAxis(axis: Axis, amount: RotationAmount): Axis = when (axis) {
		Axis.X -> Axis.Z
		Axis.Z -> Axis.X
		Axis.Y -> Axis.Y
	}