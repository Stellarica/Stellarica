package io.github.hydrazinemc.hydrazine.utils.gui.hotbar

import org.bukkit.inventory.ItemStack

// used so hotbars can be toggled between
data class HotbarState(
	var originalHotbar: MutableList<ItemStack?>,
	var menuHotbar: MutableList<ItemStack?>,
	var isMenuOpen: Boolean
)