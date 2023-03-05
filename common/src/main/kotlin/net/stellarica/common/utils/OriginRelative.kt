package net.stellarica.common.utils

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction


/**
 * Coordinates relative to the origin of something
 */
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

	companion object {
		// this would be better as a constructor
		fun getOriginRelative(loc: BlockPos, origin: BlockPos, direction: Direction): OriginRelative {
			val relative = loc.subtract(origin)
			return when (direction) {
				Direction.NORTH -> OriginRelative(relative.x, relative.y, relative.z)
				Direction.SOUTH -> OriginRelative(-relative.x, relative.y, -relative.z)
				Direction.EAST -> OriginRelative(relative.z, relative.y, relative.x)
				Direction.WEST -> OriginRelative(-relative.z, relative.y, -relative.x)
				else -> throw Exception("wtf happened here you dummy")
			}
		}

		fun getBlockPos(loc: OriginRelative, origin: BlockPos, direction: Direction): BlockPos {
			return when (direction) {
				// todo: this doesn't seem right
				Direction.NORTH -> BlockPos(loc.x, loc.y, loc.z)
				Direction.SOUTH -> BlockPos(-loc.x, loc.y, -loc.z)
				Direction.EAST -> BlockPos(loc.z, loc.y, loc.x)
				Direction.WEST -> BlockPos(-loc.z, loc.y, -loc.x)
				else -> throw Exception("wtf happened here you dummy")
			}.offset(origin)
		}
	}
}