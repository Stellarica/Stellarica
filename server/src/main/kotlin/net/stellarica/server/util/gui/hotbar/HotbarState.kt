package net.stellarica.server.util.gui.hotbar

import org.bukkit.inventory.ItemStack

/**
 * Represents the state of a player's hotbar
 */
data class HotbarState(
		var originalHotbar: List<ItemStack?>,
		var menuHotbar: MutableList<ItemStack?>,
		var isMenuOpen: Boolean
)
