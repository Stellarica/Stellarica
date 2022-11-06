package io.github.hydrazinemc.hydrazine.multiblocks

import io.github.hydrazinemc.hydrazine.utils.OriginRelative

/**
 * A type of multiblock
 */
data class MultiblockType(
	/**
	 * The name of the multiblock type
	 */
	val name: String,

	/**
	 * The ids of the origin relative blocks that define the shape of this type
	 * @see getId
	 */
	val blocks: Map<OriginRelative, String>,

	/**
	 * The function ticked on every instance of this type
	 */
	val onTick: (MultiblockInstance) -> Unit,
)
