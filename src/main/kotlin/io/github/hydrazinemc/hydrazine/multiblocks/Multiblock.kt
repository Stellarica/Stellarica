package io.github.hydrazinemc.hydrazine.multiblocks

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
