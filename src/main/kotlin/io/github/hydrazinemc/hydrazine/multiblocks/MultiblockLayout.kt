package io.github.hydrazinemc.hydrazine.multiblocks

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
