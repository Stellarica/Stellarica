package io.github.hydrazinemc.hydrazine.server.multiblocks

import io.github.hydrazinemc.hydrazine.common.utils.OriginRelative
import io.github.hydrazinemc.hydrazine.server.utils.locations.BlockLocation
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
) {

	/**
	 * @return the world location of [position]
	 */
	fun getLocation(position: OriginRelative): Location {
		return when (facing) {
			BlockFace.NORTH -> BlockLocation(position.x, position.y, position.z)
			BlockFace.SOUTH -> BlockLocation(-position.x, position.y, -position.z)
			BlockFace.EAST -> BlockLocation(-position.z, position.y, -position.x)
			BlockFace.WEST -> BlockLocation(position.z, position.y, position.x)
			else -> throw Exception("wtf happened here you dummy")
		}.asLocation.apply { this.world = origin.world }.add(origin)
	}

	/**
	 * @return the origin relative position of [loc]
	 */
	fun getOriginRelative(loc: Location): OriginRelative {
		val relative = loc.clone().subtract(origin)
		return when (facing) {
			BlockFace.NORTH -> OriginRelative(relative.blockX, relative.blockY, relative.blockZ)
			BlockFace.SOUTH -> OriginRelative(-relative.blockX, relative.blockY, -relative.blockZ)
			BlockFace.EAST -> OriginRelative(-relative.blockZ, relative.blockY, -relative.blockX)
			BlockFace.WEST -> OriginRelative(relative.blockZ, relative.blockY, relative.blockX)
			else -> throw Exception("wtf happened here you dummy")
		}
	}

	/**
	 * @return whether this contains a block at [loc].
	 */
	fun contains(loc: Location): Boolean {
		if (loc.world != origin.world) return false
		type.blocks.keys.forEach {
			if (it == getOriginRelative(loc)) return true
		}
		return false
	}
}
