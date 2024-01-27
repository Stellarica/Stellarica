package net.stellarica.common.coordinate

import kotlinx.serialization.Serializable
import net.minecraft.core.Direction
import net.stellarica.common.util.getRotFromNorth
import net.stellarica.common.util.rotateBlockPosition

@Serializable
data class BlockPosition(
	val x: Int,
	val y: Int,
	val z: Int
) {
	fun getAsRelative(origin: BlockPosition, direction: Direction): RelativeBlockPosition {
		(this - origin).run {
			return when (direction) {
				Direction.NORTH -> RelativeBlockPosition(x, y, z)
				Direction.SOUTH -> RelativeBlockPosition(-x, y, -z)
				Direction.EAST -> RelativeBlockPosition(z, y, -x)
				Direction.WEST -> RelativeBlockPosition(-z, y, x)
				else -> throw IllegalArgumentException("Invalid direction $direction")
			}
		}
	}

	operator fun minus(other: BlockPosition) = BlockPosition(x - other.x, y - other.y, z - other.z)
	operator fun plus(other: BlockPosition) = BlockPosition(x + other.x, y + other.y, z + other.z)
	operator fun times(other: Int) = BlockPosition(x * other, y * other, z * other)
}
