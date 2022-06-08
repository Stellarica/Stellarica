package io.github.hydrazinemc.hydrazine.multiblocks

import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.plugin
import io.github.hydrazinemc.hydrazine.customblocks.CustomBlocks
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.MultipleFacing

/**
 * A layout of blocks that represents a valid multiblock shape
 */
data class MultiblockLayout(
	/**
	 * The name of the layout
	 */
	val name: String
	) {
	/**
	 * The ids of the origin relative blocks
	 * @see getId
	 */
	val blocks = mutableMapOf<MultiblockOriginRelative, String>()
}

/**
 * Data for a multiblock instance
 */
data class Multiblock(
	/**
	 * The name of the [MultiblockLayout]
	 */
	val name: String,
	/**
	 * The x coordinate of the origin
	 */
	val x: Int,
	/**
	 * The y coordinate of the origin
	 */
	val y: Int,
	/**
	 * The z coordinate of the origin
	 */
	val z: Int,
	/**
	 * The amount of rotation
	 */
	val r: Byte,
	/**
	 * The number of ticks since the multiblock did something?
	 * Not sure; leftover from MSP
	 */
	var t: Int = 0
)

/**
 * Get the custom block or material id of the block
 */
fun getId(block: Block): String = (
		CustomBlocks[block.blockData as? MultipleFacing] ?: run {
			return block.type.toString().lowercase()
		}).id.lowercase()
