package io.github.hydrazinemc.hydrazine.customblocks

import io.github.hydrazinemc.hydrazine.customitems.CustomItem
import io.github.hydrazinemc.hydrazine.customitems.CustomItems
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.data.MultipleFacing
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
	private val item: String?, // don't pass the custom item because we can't be sure its registered yet
	/**
	 * The data for the underlying mushroom stem BlockData
	 */
	val data: Map<BlockFace, Boolean>,
	/**
	 * The drops on block break
	 */
	val drops: ItemStack?,
) {
	/**
	 * The custom item used to place this
	 */
	val customItem: CustomItem?
		get() = CustomItems[item]

	/**
	 * The block data of the custom block
	 */
	val blockData: MultipleFacing by lazy {
		val blockData = Bukkit.getServer().createBlockData(Material.MUSHROOM_STEM) as MultipleFacing
		data.forEach { (face, value) -> blockData.setFace(face, value) }
		blockData
	}
}
