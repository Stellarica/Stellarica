package io.github.hydrazinemc.hydrazine.crafts.pilotable.starships

import io.github.hydrazinemc.hydrazine.crafts.pilotable.Pilotable
import io.github.hydrazinemc.hydrazine.utils.Vector3
import org.bukkit.Location

class Starship(origin: Location): Pilotable(origin) {
	var velocty: Vector3 = Vector3.zero
		set(value) {field = value.clamp(Vector3.zero, maxVelocity)}
	var acceleration: Vector3 = Vector3.zero
		set(value) {field = value.clamp(Vector3.zero, maxAcceleration)}

	var maxVelocity: Vector3 = Vector3(20.0, 20.0, 20.0)
	var maxAcceleration = Vector3(5.0, 5.0, 5.0)

}