package net.stellarica.server.material.custom.block

import net.minecraft.resources.ResourceLocation
import net.stellarica.server.material.custom.item.CustomItem
import net.stellarica.server.material.type.item.ItemType

/**
 * Holds data for a custom block type
 */
data class CustomBlock(
	/** ID of the custom block */
	val id: ResourceLocation,
	/** The item used to place this block*/
	val item: CustomItem?,
	/**
	 * The drops on block break.
	 * If null this is assumed to be the same as [item]
	 */
	val drops: Map<ItemType, Int>? = null,
)