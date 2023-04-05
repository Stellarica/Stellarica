package net.stellarica.common.utils

import kotlinx.serialization.Serializable
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i


/**
 * Coordinates relative to the origin of something
 */
@Serializable
data class OriginRelative(
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
		return Companion.getBlockPos(this, origin, direction)
	}

	operator fun plus(other: OriginRelative) = OriginRelative(x + other.x, y + other.y, z + other.z)
	operator fun times(dist: Int) = OriginRelative(x * dist, y * dist, z * dist)

	companion object {
		// this would be better as a constructor
		fun getOriginRelative(loc: BlockPos, origin: BlockPos, direction: Direction): OriginRelative {
			return rotateCoordinates(loc.toVec3(), origin.toVec3(), direction.getRotFromNorth().asRadians)
				.subtract(origin.toVec3())
				.toVec3i()
				.let { OriginRelative(it.x, it.y, it.z) }
		}

		fun getBlockPos(loc: OriginRelative, origin: BlockPos, direction: Direction): BlockPos {
			return rotateCoordinates(
				loc.let { Vec3i(it.x, it.y, it.z).toVec3().add(origin.toVec3()) },
				origin.toVec3(),
				direction.getRotFromNorth().asRadians
			).toBlockPos()
		}
	}
}