package io.github.hydrazinemc.hydrazine.multiblocks

import io.github.hydrazinemc.hydrazine.utils.locations.BlockLocation

/**
 * Data for a multiblock instance
 */
data class Multiblock(
	/**
	 * The name of the [MultiblockLayout]
	 */
	val name: String,
	val origin: BlockLocation,
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
