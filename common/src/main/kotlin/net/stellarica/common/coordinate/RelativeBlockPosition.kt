package net.stellarica.common.coordinate

import kotlinx.serialization.Serializable
import net.minecraft.core.Direction
import net.minecraft.world.level.block.Rotation
import net.stellarica.common.util.getRotFromNorth
import net.stellarica.common.util.rotateBlockPosition


/** Represents coordinates relative to some other position */
@Serializable
data class RelativeBlockPosition(
	val x: Int,
	val y: Int,
	val z: Int
) {
	fun getGlobalPosition(origin: BlockPosition, direction: Direction): BlockPosition {
		return when (direction) {
			Direction.NORTH -> BlockPosition(x, y, z)
			Direction.SOUTH -> BlockPosition(-x, y, -z)
			Direction.EAST -> BlockPosition(-z, y, x)
			Direction.WEST -> BlockPosition(z, y, -x)
			else -> throw IllegalArgumentException("Invalid direction $direction")
		} + origin
	}

	operator fun plus(other: RelativeBlockPosition) = RelativeBlockPosition(x + other.x, y + other.y, z + other.z)
	operator fun minus(other: RelativeBlockPosition) = RelativeBlockPosition(x - other.x, y - other.y, z - other.z)
	operator fun times(dist: Int) = RelativeBlockPosition(x * dist, y * dist, z * dist)
}
