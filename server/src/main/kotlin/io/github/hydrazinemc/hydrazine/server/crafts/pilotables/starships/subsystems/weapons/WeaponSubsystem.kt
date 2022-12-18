package io.github.hydrazinemc.hydrazine.server.crafts.pilotables.starships.subsystems.weapons

import DebugProjectile
import io.github.hydrazinemc.hydrazine.server.crafts.pilotables.starships.Starship
import io.github.hydrazinemc.hydrazine.server.crafts.pilotables.starships.subsystems.Subsystem
import io.github.hydrazinemc.hydrazine.server.multiblocks.MultiblockInstance
import org.bukkit.util.Vector
import java.lang.ref.WeakReference
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin

class WeaponSubsystem(ship: Starship) : Subsystem(ship) {

	val multiblocks = mutableSetOf<WeakReference<MultiblockInstance>>()

	override fun onShipPiloted() {
		ship.multiblocks.forEach { multiblock ->
			if (multiblock.get()?.type in WeaponType.values().map { it.multiblockType }) {
				multiblocks.add(multiblock)
			}
		}
	}

	fun fire() {

		// the direction the pilot is facing
		val eye = ship.pilot!!.eyeLocation.direction.normalize()

		WeaponType.values().sortedBy{it.priority}.forEach { type ->
			multiblocks.mapNotNull{it.get()}.filter{ it.type == type.multiblockType }.forEach { multiblock ->

				// the direction the weapon is facing
				val direction = multiblock.getLocation(type.direction).clone().subtract(multiblock.getLocation(type.mount)).toVector().normalize()

				// if the angleBetween (in radians) is less than the type's cone, fire
				if (abs(eye.angle(direction)) > type.cone + (PI / 4)) return@forEach

				// this whole thing could definitely be improved
				val dirPitch = asin(-direction.y)
				val dirYaw = atan2(direction.x, direction.z)
				val eyePitch = asin(-eye.y)
				val eyeYaw = atan2(eye.x, eye.z)

				val pitch = eyePitch.coerceIn(dirPitch - type.cone, dirPitch + type.cone)
				val yaw = if (eyeYaw - type.cone > -PI) {
					eyeYaw.coerceIn(dirYaw - type.cone, dirYaw + type.cone)
				} else { // fix for atan2 range not going beneath -PI, breaking coerceIn
					eyeYaw
				}

				val dir = Vector(
					sin(yaw) * cos(pitch),
					-sin(pitch),
					cos(yaw) * cos(pitch)
				)

				val adjDirYaw = dirYaw - (2 * PI)
				if (eyeYaw != eyeYaw.coerceIn(adjDirYaw - type.cone, adjDirYaw + type.cone) && direction.z < 0 && eye.z < 0 && eye.x < 0) {
					// more fix for arctan range issues
					// literal duct tape
					dir.x = -dir.x
				}
				//println("e: $eyeYaw, d: $adjDirYaw, c: ${type.cone} d-e${adjDirYaw - type.cone} d+e${adjDirYaw + type.cone}")

				type.projectile.shoot(
					ship,
					multiblock.getLocation(type.mount).clone().add(0.5, 0.5, 0.5).add(dir)
						.also { it.direction = dir }
				)
			}
		}
	}
}