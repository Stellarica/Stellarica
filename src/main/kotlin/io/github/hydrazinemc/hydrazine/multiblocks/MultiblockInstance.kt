package io.github.hydrazinemc.hydrazine.multiblocks

import org.bukkit.Location
import org.bukkit.block.BlockFace
import java.util.UUID

/**
 * Data for a multiblock instance
 */
data class MultiblockInstance(
	/**
	 * The name of the [MultiblockType]
	 */
	val name: String,
	/**
	 * Unique ID for this multiblock
	 */
	val uuid: UUID,
	/**
	 * The origin of the multiblock
	 */
	var origin: Location,
	/**
	 * The direction it is facing/oriented
	 */
	var facing: BlockFace,
)
