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
			if (multiblock.type in WeaponType.values().map { it.multiblockType }) {
				multiblocks.add(WeakReference(multiblock))
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
				val dir = if (abs(eye.angle(direction)) < type.cone + (PI / 4)) {
					// this whole thing could definitely be improved
					val dirPitch = asin(-direction.y)
					val dirYaw = atan2(direction.x, direction.z)

					val pitch = asin(-eye.y).coerceIn(dirPitch - type.cone, dirPitch + type.cone)
					val yaw = atan2(eye.x, eye.z).coerceIn(dirYaw - type.cone, dirYaw + type.cone)

					Vector(
						sin(yaw) * cos(pitch),
						-sin(pitch),
						cos(yaw) * cos(pitch),
					)
				}
				else return@forEach

				DebugProjectile.shoot(ship,
					multiblock.getLocation(type.mount).clone().add(0.5, 0.5, 0.5).add(direction)
						.also { it.direction = direction }
					)

				DebugProjectile.shoot(ship,
					multiblock.getLocation(type.mount).clone().add(0.5, 0.5, 0.5).add(eye)
						.also { it.direction = eye }
				)

				type.projectile.shoot(
					ship,
					multiblock.getLocation(type.mount).clone().add(0.5, 0.5, 0.5).add(dir)
						.also { it.direction = dir }
				)
			}
		}
	}
}