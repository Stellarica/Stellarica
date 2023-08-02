package net.stellarica.server.util.gui.hotbar

import org.bukkit.inventory.ItemStack

/**
 * Represents the state of a player's hotbar
 */
data class HotbarState(
	/**
	 * The player's original hotbar
	 */
	var originalHotbar: List<ItemStack?>,
	/**
	 * The hotbar of the HotbarMenu
	 */
	var menuHotbar: MutableList<ItemStack?>,
	/**
	 * Whether the player's current hotbar is [menuHotbar]
	 */
	var isMenuOpen: Boolean
)
