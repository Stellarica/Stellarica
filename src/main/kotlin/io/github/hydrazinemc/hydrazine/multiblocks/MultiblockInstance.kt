package io.github.hydrazinemc.hydrazine.multiblocks

import io.github.hydrazinemc.hydrazine.utils.locations.BlockLocation
import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.bukkit.persistence.PersistentDataContainer
import java.lang.Exception
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
) {
	fun getLocation(position: MultiblockOriginRelative): Location {
		return when (facing) {
			BlockFace.NORTH -> BlockLocation(position.x, position.y, position.z, origin.world)
			BlockFace.SOUTH -> BlockLocation(-position.x, position.y, -position.z)
			BlockFace.EAST -> BlockLocation(position.z, position.y, position.x)
			BlockFace.WEST -> BlockLocation(-position.z, position.y, -position.x)
			else -> throw Exception("wtf happened here you dummy")
		}.asLocation.add(origin)
	}

	fun getOriginRelative(loc: Location): MultiblockOriginRelative {
		val relative = loc.subtract(origin)
		return when (facing) {
			BlockFace.NORTH -> MultiblockOriginRelative(relative.blockX, relative.blockY, relative.blockZ)
			BlockFace.SOUTH -> MultiblockOriginRelative(-relative.blockX, relative.blockY, -relative.blockZ)
			BlockFace.EAST -> MultiblockOriginRelative(relative.blockZ, relative.blockY, relative.blockX)
			BlockFace.WEST -> MultiblockOriginRelative(-relative.blockZ, relative.blockY, -relative.blockX)
			else -> throw Exception("wtf happened here you dummy")
		}
	}

	fun contains(loc: BlockLocation): Boolean {
		return false
	}
}
