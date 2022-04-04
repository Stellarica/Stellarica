package io.github.hydrazinemc.hydrazine.utils

import org.bukkit.Location
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

data class Vector3(var x: Double, var y: Double, var z: Double) {
	constructor(loc: BlockLocation) : this(loc.x.toDouble(), loc.y.toDouble(), loc.z.toDouble())
	constructor(loc: Location) : this(loc.x, loc.y, loc.z)

	operator fun plus(other: Vector3) = Vector3(x + other.x, y + other.y, z + other.z)
	operator fun minus(other: Vector3) = Vector3(x - other.x, y - other.y, z - other.z)
	operator fun times(other: Float) = Vector3(x * other, y * other, z * other)

	val asLocation: Location = Location(null, x, y, z)
	val asBlockLocation: BlockLocation = BlockLocation(x.roundToInt(), y.roundToInt(), z.roundToInt(), null)
}


fun rotateCoordinates(loc: Vector3, origin: Vector3, theta: Double): Vector3 = Vector3(
	origin.x + (((loc.x - origin.x) * cos(theta)) - ((loc.z - origin.z) * sin(theta))),
	loc.y,  // too many parentheses is better than too few
	origin.z + (((loc.x - origin.x) * sin(theta)) + ((loc.z - origin.z) * cos(theta))),
)