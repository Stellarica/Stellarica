package io.github.hydrazinemc.hydrazine.crafts.pilotable.starships

import io.github.hydrazinemc.hydrazine.crafts.pilotable.Pilotable
import io.github.hydrazinemc.hydrazine.utils.Vector3
import org.bukkit.Location

/**
 * Base Starship class
 */
class Starship(origin: Location) : Pilotable(origin) {
	/**
	 * The velocity (in blocks per second) of this starship
	 * @see maxVelocity
	 */
	var velocty: Vector3 = Vector3.zero
		set(value) {
			field = value.clamp(Vector3.zero, maxVelocity)
		}

	/**
	 * The current acceleration of this starship, in blocks per second per second
	 * @see maxAcceleration
	 */
	var acceleration: Vector3 = Vector3.zero
		set(value) {
			field = value.clamp(Vector3.zero, maxAcceleration)
		}

	/**
	 * The maximum velocity this starship can travel at
	 * @see velocty
	 */
	var maxVelocity: Vector3 = Vector3(20.0, 20.0, 20.0)

	/**
	 * The maximum acceleration this starship can attain
	 * @see acceleration
	 */
	var maxAcceleration = Vector3(5.0, 5.0, 5.0)
}
