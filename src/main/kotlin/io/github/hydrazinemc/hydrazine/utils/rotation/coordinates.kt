package io.github.hydrazinemc.hydrazine.utils.rotation

import io.github.hydrazinemc.hydrazine.utils.Vector3
import kotlin.math.cos
import kotlin.math.sin

/**
 * Rotate [loc] around [origin] by [theta] radians.
 * Note, [theta] positive = clockwise, negative = counter clockwise
 * @see Vector3.rotateAround
 */
fun rotateCoordinates(loc: Vector3, origin: Vector3, theta: Double): Vector3 = Vector3(
	origin.x + (((loc.x - origin.x) * cos(theta)) - ((loc.z - origin.z) * sin(theta))),
	loc.y,  // too many parentheses is better than too few
	origin.z + (((loc.x - origin.x) * sin(theta)) + ((loc.z - origin.z) * cos(theta))),
)

/**
 * Rotate [loc] [rotation] around [origin]
 * @see Vector3.rotateAround
 */
fun rotateCoordinates(loc: Vector3, origin: Vector3, rotation: RotationAmount): Vector3 =
	rotateCoordinates(loc, origin, rotation.asRadians)
