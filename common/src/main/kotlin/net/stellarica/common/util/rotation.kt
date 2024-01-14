package net.stellarica.common.util

import net.minecraft.core.Direction
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.phys.Vec3
import net.stellarica.common.coordinate.BlockPosition
import kotlin.math.cos
import kotlin.math.sin

/**
 * Rotate [loc] around [origin] by [theta] radians.
 * Note, [theta] positive = clockwise, negative = counter clockwise
 */
fun rotateCoordinates(loc: Vec3, origin: Vec3, theta: Double): Vec3 = Vec3(
		origin.x + (((loc.x - origin.x) * cos(theta)) - ((loc.z - origin.z) * sin(theta))),
		loc.y,  // too many parentheses is better than too few
		origin.z + (((loc.x - origin.x) * sin(theta)) + ((loc.z - origin.z) * cos(theta))),
)

fun rotateCoordinates(loc: Vec3, origin: Vec3, rotation: Rotation): Vec3 =
		rotateCoordinates(loc, origin, rotation.asRadians)

fun rotateBlockPosition(pos: BlockPosition, origin: BlockPosition, rotation: Rotation): BlockPosition =
		rotateCoordinates(pos.toVec3(), origin.toVec3(), rotation).toBlockPosition()

val Rotation.asRadians: Double
	get() = when (this) {
		Rotation.NONE -> 0.0
		Rotation.CLOCKWISE_90 -> Math.PI / 2
		Rotation.CLOCKWISE_180 -> Math.PI
		Rotation.COUNTERCLOCKWISE_90 -> -Math.PI / 2
	}

val Rotation.asDegrees: Double
	get() = Math.toDegrees(asRadians) // :iea:

fun Direction.rotate(rot: Rotation): Direction = when (rot) {
	Rotation.NONE -> this
	Rotation.CLOCKWISE_90 -> this.clockWise
	Rotation.CLOCKWISE_180 -> this.opposite
	Rotation.COUNTERCLOCKWISE_90 -> this.counterClockWise
}

fun Direction.getRotFromNorth() = when (this) {
	Direction.NORTH -> Rotation.NONE
	Direction.EAST -> Rotation.CLOCKWISE_90
	Direction.SOUTH -> Rotation.CLOCKWISE_180
	Direction.WEST -> Rotation.COUNTERCLOCKWISE_90
	else -> throw Exception("don't be lazy")
}