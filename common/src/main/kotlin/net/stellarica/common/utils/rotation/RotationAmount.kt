package net.stellarica.common.utils.rotation

/**
 * Represents an amount of rotation
 * @param asRadians this in radians
 * @param asDegrees this in degrees
 */
enum class RotationAmount(val asRadians: Double = 0.0, val asDegrees: Float = 0f) {
	CLOCKWISE(Math.PI / 2, 90f),
	COUNTERCLOCKWISE(-Math.PI / 2, -90f),
	REVERSE(Math.PI, 180f),
	NONE,
}
