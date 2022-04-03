package io.github.hydrazinemc.hydrazine.utils

import org.bukkit.Location
import kotlin.math.roundToInt

data class Vector3(var x: Double, var y: Double, var z: Double) {
	constructor(loc: BlockLocation) : this(loc.x.toDouble(), loc.y.toDouble(), loc.z.toDouble())
	constructor(loc: Location) : this(loc.x, loc.y, loc.z)

	operator fun plus(other: Vector3) = Vector3(x + other.x, y + other.y, z + other.z)
	operator fun minus(other: Vector3) = Vector3(x - other.x, y - other.y, z - other.z)
	operator fun times(other: Float) = Vector3(x * other, y * other, z * other)

	val asLocation: Location = Location(null, x, y, z)
	val asBlockLocation: BlockLocation = BlockLocation(x.roundToInt(), y.roundToInt(), z.roundToInt(), null)
}