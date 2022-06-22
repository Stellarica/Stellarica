package io.github.hydrazinemc.hydrazine.multiblocks

import org.bukkit.Location
import org.bukkit.block.BlockFace
import java.util.UUID

/**
 * Data for a multiblock instance
 */
data class Multiblock(
	/**
	 * The name of the [MultiblockLayout]
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
	/**
	 * The number of ticks since the multiblock did something?
	 * Not sure; leftover from MSP
	 */
	var t: Int = 0
)
