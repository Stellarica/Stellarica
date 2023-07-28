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
		// todo: don't use rotateCoordinates, use a when statement like getBlockPos
		return (rotateBlockPosition(this, origin, direction.getRotFromNorth()) - origin)
			.let { RelativeBlockPosition(it.x, it.y, it.z) }
	}

	operator fun minus(other: BlockPosition) = BlockPosition(x - other.x, y - other.y, z - other.z)
	operator fun plus(other: BlockPosition) = BlockPosition(x + other.x, y + other.y, z + other.z)
	operator fun times(other: Int) = BlockPosition(x * other, y * other, z * other)
}