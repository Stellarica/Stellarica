package net.stellarica.server.material.block

import net.minecraft.resources.ResourceLocation
import net.stellarica.server.material.item.CustomItem
import net.stellarica.server.material.item.type.ItemType
import org.bukkit.Instrument
import org.bukkit.Note

/**
 * Holds data for a custom block type
 */
class CustomBlock(
		/** ID of the custom block */
		val id: ResourceLocation,
		/** The item used to place this block*/
		val item: CustomItem?,
		/**
		 * The drops on block break.
		 * If null this is assumed to be the same as [item]
		 */
		val drops: Map<ItemType, Int>? = null,
		val note: Note,
		val instrument: Instrument
)