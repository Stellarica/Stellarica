package net.stellarica.common.coord

import kotlinx.serialization.Serializable
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.stellarica.common.util.*


/**
 * Coordinates relative to the origin of something
 */
@Serializable
data class RelativePosition(
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
) {

	fun getBlockPos(origin: BlockPos, direction: Direction): BlockPos {
		return getBlockPos(this, origin, direction)
	}

	operator fun plus(other: RelativePosition) = RelativePosition(x + other.x, y + other.y, z + other.z)
	operator fun times(dist: Int) = RelativePosition(x * dist, y * dist, z * dist)

	companion object {
		// this would be better as a constructor
		fun getOriginRelative(loc: BlockPos, origin: BlockPos, direction: Direction): RelativePosition {
			// todo: don't use rotateCoordinates, use a when statement like getBlockPos
			return rotateCoordinates(loc.toVec3(), origin.toVec3(), direction.getRotFromNorth().asRadians)
					.subtract(origin.toVec3())
					.toVec3i()
					.let { RelativePosition(it.x, it.y, it.z) }
		}

		fun getBlockPos(loc: RelativePosition, origin: BlockPos, direction: Direction): BlockPos {
			return when (direction) {
				Direction.NORTH -> BlockPos(loc.x, loc.y, loc.z)
				Direction.EAST -> BlockPos(-loc.z, loc.y, loc.x)
				Direction.SOUTH -> BlockPos(-loc.x, loc.y, -loc.z)
				Direction.WEST -> BlockPos(loc.z, loc.y, -loc.x)
				else -> throw IllegalArgumentException()
			}.offset(origin)
		}
	}
}