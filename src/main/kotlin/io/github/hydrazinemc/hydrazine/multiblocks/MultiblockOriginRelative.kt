package io.github.hydrazinemc.hydrazine.multiblocks

/**
 * Coordinates relative to the origin of a multiblock
 */
data class MultiblockOriginRelative(
	/**
	 * The x component
	 */
	val x: Int,
	/**
	 * The y component
	 */
	val y: Int,
	/**
	 * The z component
	 */
	val z: Int
)
