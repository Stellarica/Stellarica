package net.stellarica.server.material.custom.block

import net.stellarica.server.material.type.item.ItemType
import org.bukkit.block.BlockFace
import org.bukkit.inventory.ItemStack

/**
 * Holds data for a custom block type
 */
data class CustomBlock(
	/**
	 * Custom block id, must be unique
	 */
	val id: String,
	/**
	 * The custom item id for this block
	 */
	private val item: ItemType,
	/**
	 * The data for the underlying mushroom stem BlockData
	 */
	val data: Map<BlockFace, Boolean>,
	/**
	 * The drops on block break
	 */
	val drops: ItemStack?,
)