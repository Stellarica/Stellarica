package net.stellarica.common.coordinate

import kotlinx.serialization.Serializable
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction


/**
 * Coordinates relative to the origin of something
 */
@Serializable
data class RelativeBlockPosition(
    val x: Int,
    val y: Int,
    val z: Int
) {
    fun getGlobalPosition(origin: BlockPosition, direction: Direction): BlockPosition {
        return when (direction) {
            Direction.NORTH -> BlockPosition(this.x, this.y, this.z)
            Direction.EAST -> BlockPosition(-this.z, this.y, this.x)
            Direction.SOUTH -> BlockPosition(-this.x, this.y, -this.z)
            Direction.WEST -> BlockPosition(this.z, this.y, -this.x)
            else -> throw IllegalArgumentException()
        } + origin
    }

    operator fun plus(other: RelativeBlockPosition) = RelativeBlockPosition(x + other.x, y + other.y, z + other.z)
    operator fun times(dist: Int) = RelativeBlockPosition(x * dist, y * dist, z * dist)
}