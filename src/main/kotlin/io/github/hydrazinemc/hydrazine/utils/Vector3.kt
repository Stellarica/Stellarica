package io.github.hydrazinemc.hydrazine.utils

import org.bukkit.Location
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * A container for three Doubles, [x], [y], and [z]
 */
data class Vector3(var x: Double, var y: Double, var z: Double) {
	companion object {
		val zero: Vector3
			get() = Vector3(0.0, 0.0, 0.0)
	}
	constructor(loc: BlockLocation) : this(loc.x.toDouble(), loc.y.toDouble(), loc.z.toDouble())
	constructor(loc: Location) : this(loc.x, loc.y, loc.z)

	operator fun plus(other: Vector3) = Vector3(x + other.x, y + other.y, z + other.z)
	operator fun minus(other: Vector3) = Vector3(x - other.x, y - other.y, z - other.z)
	operator fun times(other: Float) = Vector3(x * other, y * other, z * other)

	/**
	 * This Vector3 as a loction.
	 * The world component of the Location will be null
	 * @see BlockLocation.asLocation
	 * @see asBlockLocation
	 * @see Location
	 */
	val asLocation: Location = Location(null, x, y, z)

	/**
	 * This Vector3 as a BlockLocation
	 * The world component of the BlockLocation will be null
	 *
	 * Note, some precision will be lost, as BlockLocations can only store integers.
	 *
	 * @see BlockLocation
	 * @see asLocation
	 * @see Location
	 */
	val asBlockLocation: BlockLocation = BlockLocation(x.roundToInt(), y.roundToInt(), z.roundToInt(), null)

	/**
	 * Get this position rotated around [origin] by [theta] radians
	 * @see rotateCoordinates
	 */
	fun rotateAround(origin: Vector3, theta: Double) = rotateCoordinates(this, origin, theta)

	/**
	 * Get this position rotated around [origin] by [rotation]
	 * @see rotateCoordinates
	 */
	fun rotateAround(origin: Vector3, rotation: RotationAmount) = rotateCoordinates(this, origin, rotation)

	fun clamp(min: Vector3, max: Vector3) = Vector3(
		x.coerceIn(min.x, max.x),
		y.coerceIn(min.y, max.y),
		z.coerceIn(min.z, max.z)
	)
}


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
