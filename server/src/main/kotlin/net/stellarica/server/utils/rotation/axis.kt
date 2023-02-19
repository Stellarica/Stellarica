package net.stellarica.server.utils.rotation

import org.bukkit.Axis

/**
 * Rotate [axis] around the y axis
 * X -> Z, Z -> X, Y -> Y
 */
fun rotateAxis(axis: Axis): Axis = when (axis) {
	Axis.X -> Axis.Z
	Axis.Z -> Axis.X
	Axis.Y -> Axis.Y
}
