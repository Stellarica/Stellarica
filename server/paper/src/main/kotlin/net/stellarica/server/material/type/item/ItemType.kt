package net.stellarica.server.material.type.item

import org.bukkit.inventory.ItemStack

interface ItemType {
	fun getItemStack(): ItemStack

}