package net.stellarica.server.crafts.starships.subsystems.weapons

import net.stellarica.common.utils.OriginRelative
import net.stellarica.common.utils.toVec3
import net.stellarica.server.crafts.starships.Starship
import net.stellarica.server.crafts.starships.subsystems.Subsystem
import net.stellarica.server.multiblocks.MultiblockType
import net.stellarica.server.utils.extensions.toLocation
import net.stellarica.server.utils.extensions.toVector
import org.bukkit.util.Vector
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class WeaponSubsystem(ship: Starship) : Subsystem(ship) {

	val multiblocks = mutableSetOf<OriginRelative>()

	override fun onShipPiloted() {
		ship.multiblocks.forEach { multiblock ->
			if (ship.getMultiblock(multiblock)?.type in WeaponType.values().map { it.multiblock }) {
				multiblocks.add(multiblock)
			}
		}
	}

	fun fireLight() = fire(setOf(WeaponType.TEST_INSTANT_WEAPON, WeaponType.TEST_LINEAR_WEAPON))
	fun fireHeavy() = fire(setOf(WeaponType.TEST_ACCELERATING_WEAPON))

	fun fire(types: Set<WeaponType>) {

		// the direction the pilot is facing
		val eye = ship.pilot!!.eyeLocation.direction.normalize()

		types.forEach { type ->
			multiblocks.map { ship.getMultiblock(it) }.filter { it?.type == type.multiblock }
				.forEach { multiblock ->

					// the direction the weapon is facing
					val direction =
						multiblock!!.getLocation(type.direction).immutable()
							.subtract(multiblock.getLocation(type.mount))
							.toVec3().normalize()

					// if the angleBetween (in radians) is less than the type's cone, fire
					if (abs(eye.angle(direction.toVector())) > type.cone + (PI / 4)) return@forEach

					// this whole thing could definitely be improved
					val dirPitch = asin(-direction.y)
					val dirYaw = atan2(direction.x, direction.z)
					val eyePitch = asin(-eye.y)
					val eyeYaw = atan2(eye.x, eye.z)

					val pitch = eyePitch // .coerceIn(dirPitch - type.cone, dirPitch + type.cone)  //this check had been seized by the balancing department
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
					if (eyeYaw != eyeYaw.coerceIn(
							adjDirYaw - type.cone,
							adjDirYaw + type.cone
						) && direction.z < 0 && eye.z < 0 && eye.x < 0
					) {
						// more fix for arctan range issues
						// literal duct tape
						dir.x = -dir.x
					}
					//println("e: $eyeYaw, d: $adjDirYaw, c: ${type.cone} d-e${adjDirYaw - type.cone} d+e${adjDirYaw + type.cone}")

					type.projectile.shoot(
						ship,
						multiblock.getLocation(type.mount).toLocation(ship.world.world).add(0.5, 0.5, 0.5).add(dir)
							.also { it.direction = dir }
					)
				}
		}
	}
}