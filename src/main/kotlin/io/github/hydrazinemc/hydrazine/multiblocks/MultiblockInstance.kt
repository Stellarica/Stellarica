package io.github.hydrazinemc.hydrazine.multiblocks

import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.bukkit.persistence.PersistentDataContainer
import java.util.UUID

/**
 * Data for a multiblock instance
 */
data class MultiblockInstance(
	/**
	 * The [MultiblockType] of this instance
	 */
	val type: MultiblockType,
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
	 * Data for this instance
	 */
	val data: PersistentDataContainer
)
