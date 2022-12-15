package io.github.hydrazinemc.hydrazine.server.multiblocks

import io.github.hydrazinemc.hydrazine.server.utils.OriginRelative

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
)
