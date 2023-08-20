package net.stellarica.server.craft

import net.stellarica.common.coordinate.BlockPosition

interface CraftContainer {
	fun addSubCraft(craft: Craft)
	fun removeSubCraft(craft: Craft)
	fun subcraftsContain(block: BlockPosition): Boolean
}