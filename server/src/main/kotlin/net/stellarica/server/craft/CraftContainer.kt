package net.stellarica.server.craft

interface CraftContainer: Craft {
	fun addSubCraft(craft: Craft)
	fun removeSubCraft(craft: Craft)
}