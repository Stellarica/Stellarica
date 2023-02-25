package net.stellarica.server.utils

import net.minecraft.core.Direction
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.phys.Vec3
import kotlin.math.cos
import kotlin.math.sin

/**
 * Rotate [loc] around [origin] by [theta] radians.
 * Note, [theta] positive = clockwise, negative = counter clockwise
 * @see Vec3.rotateAround
 */
fun rotateCoordinates(loc: Vec3, origin: Vec3, theta: Double): Vec3 = Vec3(
	origin.x + (((loc.x - origin.x) * cos(theta)) - ((loc.z - origin.z) * sin(theta))),
	loc.y,  // too many parentheses is better than too few
	origin.z + (((loc.x - origin.x) * sin(theta)) + ((loc.z - origin.z) * cos(theta))),
)

/**
 * Rotate [loc] [rotation] around [origin]
 * @see Vec3.rotateAround
 */
fun rotateCoordinates(loc: Vec3, origin: Vec3, rotation: Rotation): Vec3 =
	rotateCoordinates(loc, origin, rotation.asRadians)


val Rotation.asRadians: Double
	get() = when (this) {
		Rotation.NONE -> 0.0
		Rotation.CLOCKWISE_90 -> Math.PI / 2
		Rotation.CLOCKWISE_180 -> Math.PI
		Rotation.COUNTERCLOCKWISE_90 -> -Math.PI / 2
	}

val Rotation.asDegrees: Double
	get() = Math.toDegrees(asRadians) // :iea:

fun Direction.rotate(rot: Rotation) = when (rot) {
	Rotation.NONE -> this
	Rotation.CLOCKWISE_90 -> this.clockWise
	Rotation.CLOCKWISE_180 -> this.opposite
	Rotation.COUNTERCLOCKWISE_90 -> this.counterClockWise
}