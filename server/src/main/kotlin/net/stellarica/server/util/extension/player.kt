package net.stellarica.server.util.extension

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack


/**
 * The player's current hotbar
 */
var Player.hotbar: MutableList<ItemStack?>
	get() = MutableList(9) { index -> this.inventory.getItem(index) }
	set(value) {
		for (i in 0..8) this.inventory.setItem(i, value[i])
	}
