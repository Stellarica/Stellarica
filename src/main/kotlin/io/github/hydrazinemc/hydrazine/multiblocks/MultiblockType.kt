package io.github.hydrazinemc.hydrazine.multiblocks

/**
 * A layout of blocks that represents a valid multiblock shape
 */
data class MultiblockType(
	/**
	 * The name of the multiblock type
	 */
	val name: String,

	/**
	 * The ids of the origin relative blocks
	 * @see getId
	 */
	val blocks: Map<MultiblockOriginRelative, String>,

	/**
	 * The function ticked on every instance of this type
	 */
	val onTick: (MultiblockInstance) -> Unit,
)
