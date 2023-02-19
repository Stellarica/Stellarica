package net.stellarica.server.utils.rotation

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
fun rotateCoordinates(loc: Vec3, origin: Vec3, rotation: RotationAmount): Vec3 =
	rotateCoordinates(loc, origin, rotation.asRadians)
