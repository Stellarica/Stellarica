package net.stellarica.common.coord

import kotlinx.serialization.Serializable
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.stellarica.common.util.*


/**
 * Coordinates relative to the origin of something
 */
@Serializable
data class RelativeBlockPosition(
		val x: Int,
		val y: Int,
		val z: Int
) {
	fun getBlockPosition(origin: BlockPos, direction: Direction): BlockPos {
		return when (direction) {
			Direction.NORTH -> BlockPos(this.x, this.y, this.z)
			Direction.EAST -> BlockPos(-this.z, this.y, this.x)
			Direction.SOUTH -> BlockPos(-this.x, this.y, -this.z)
			Direction.WEST -> BlockPos(this.z, this.y, -this.x)
			else -> throw IllegalArgumentException()
		}.offset(origin)
	}

	operator fun plus(other: RelativeBlockPosition) = RelativeBlockPosition(x + other.x, y + other.y, z + other.z)
	operator fun times(dist: Int) = RelativeBlockPosition(x * dist, y * dist, z * dist)
}