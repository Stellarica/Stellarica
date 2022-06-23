package io.github.hydrazinemc.hydrazine.utils

import io.github.hydrazinemc.hydrazine.utils.locations.BlockLocation
import io.github.hydrazinemc.hydrazine.utils.rotation.RotationAmount
import io.github.hydrazinemc.hydrazine.utils.rotation.rotateCoordinates
import org.bukkit.Location
import org.bukkit.util.Vector
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * A container for three Doubles, [x], [y], and [z]
 *
 * @see BlockLocation
 * @see Vector
 */
data class Vector3(var x: Double, var y: Double, var z: Double) {
	companion object {
		/**
		 * Null vector
		 */
		val zero: Vector3
			get() = Vector3(0.0, 0.0, 0.0)
	}

	constructor(loc: BlockLocation) : this(loc.x.toDouble(), loc.y.toDouble(), loc.z.toDouble())
	constructor(loc: Location) : this(loc.x, loc.y, loc.z)
	constructor(vec: Vector) : this(vec.x, vec.y, vec.z)

	/**
	 * Add this to [other]
	 */
	operator fun plus(other: Vector3) = Vector3(x + other.x, y + other.y, z + other.z)

	/**
	 * Subtract this from [other]
	 */
	operator fun minus(other: Vector3) = Vector3(x - other.x, y - other.y, z - other.z)

	/**
	 * Multiply this by [other]
	 */
	operator fun times(other: Float) = Vector3(x * other, y * other, z * other)

	/**
	 * Multiply this by [other]
	 */
	operator fun times(other: Double) = Vector3(x * other, y * other, z * other)

	/**
	 * Divide this by [other]
	 */
	operator fun div(other: Float) = Vector3(x / other, y / other, z / other)

	/**
	 * Divide this by [other]
	 */
	operator fun div(other: Double) = Vector3(x / other, y / other, z / other)

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
	 * This Vector3 as a string with MiniMessage formatting
	 */
	val miniMessage: String
		get() = "(<b>$x</b>, <b>$y</b>, <b>$z</b>)"

	/**
	 * Get this position rotated around [origin] by [theta] radians
	 * @see rotateCoordinates
	 */
	fun rotateAround(origin: Vector3, theta: Double) =
		rotateCoordinates(this, origin, theta)

	/**
	 * Get this position rotated around [origin] by [rotation]
	 * @see rotateCoordinates
	 */
	fun rotateAround(origin: Vector3, rotation: RotationAmount) = rotateCoordinates(this, origin, rotation)

	/**
	 * Clamp this vector's components by [min] and [max]
	 */
	fun clamp(min: Vector3, max: Vector3) = Vector3(
		x.coerceIn(min.x, max.x),
		y.coerceIn(min.y, max.y),
		z.coerceIn(min.z, max.z)
	)

	/**
	 * Get the distance between this and [other]
	 * Note that this uses sqare root calculations and isn't very performant.
	 * @see distanceSquared for comparing distances
	 */ // todo: handle 0 distance
	fun distance(other: Vector3) = sqrt(distanceSquared(other))

	/**
	 * The distance between this and [other], squared
	 * @see distance
	 */
	fun distanceSquared(other: Vector3) =
		(x - other.x).pow(2.0) + (y - other.y).pow(2.0) + (z - other.z).pow(2.0)


	/**
	 * This vector, inverted
	 */
	operator fun unaryMinus(): Vector3 = Vector3(-x, -y, -z)

	/**
	 * The magnetude of this vector
	 */
	val magnetude: Double
		get() = if (this.length == 0.0) {0.0} else{distance(zero)}

	/**
	 * The length of this vector's components, summed
	 */
	val length: Double
		get() = x + y + z

	/**
	 * This vector but with a magnitude of 1
	 */
	val normalized: Vector3
		get() = this / magnetude
}
