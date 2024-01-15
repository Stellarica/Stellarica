package net.stellarica.server.craft

import net.stellarica.common.coordinate.BlockPosition

interface CraftContainer {
	val subcrafts: Collection<Subcraft>
	fun addSubCraft(craft: Subcraft)
	fun removeSubCraft(craft: Subcraft)
	fun subcraftsContain(block: BlockPosition): Boolean
}
