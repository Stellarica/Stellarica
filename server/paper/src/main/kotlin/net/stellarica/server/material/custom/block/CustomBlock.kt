package net.stellarica.server.material.custom.block

import net.minecraft.resources.ResourceLocation
import net.stellarica.server.material.type.item.ItemType

/**
 * Holds data for a custom block type
 */
data class CustomBlock(
	/** ID of the custom block */
	val id: ResourceLocation,
	/** The item used to place this block*/
	private val item: ItemType?,
	/** The drops on block break.*/
	val drops: Map<ItemType, Int>,
)