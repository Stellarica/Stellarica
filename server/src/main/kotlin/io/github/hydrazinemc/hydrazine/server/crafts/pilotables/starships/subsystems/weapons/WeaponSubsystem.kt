package io.github.hydrazinemc.hydrazine.server.crafts.pilotables.starships.subsystems.weapons

import DebugProjectile
import io.github.hydrazinemc.hydrazine.server.crafts.pilotables.starships.Starship
import io.github.hydrazinemc.hydrazine.server.crafts.pilotables.starships.subsystems.Subsystem
import io.github.hydrazinemc.hydrazine.server.multiblocks.MultiblockInstance
import io.github.hydrazinemc.hydrazine.server.utils.extensions.abs
import io.github.hydrazinemc.hydrazine.server.utils.extensions.sendRichMessage
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
		val eye = ship.pilot!!.eyeLocation.direction.normalize()

		WeaponType.values().sortedBy{it.priority}.forEach { type ->
			multiblocks.mapNotNull{it.get()}.filter{ it.type == type.multiblockType }.forEach { multiblock ->


				val direction = multiblock.getLocation(type.direction).clone().subtract(multiblock.getLocation(type.mount)).toVector().normalize()
				val angleBetween = abs(eye.angle(direction))

				// if the angleBetween (in radians) is less than the type's cone, fire
				val dir = if (angleBetween < type.cone + (PI / 4)) {
					// this whole thing could definitely be improved
					val relative = eye.clone().subtract(direction).normalize()

					val pitch = asin(-relative.y).coerceIn(-type.cone, type.cone)
					val yaw = atan2(relative.x, relative.z).coerceIn(-type.cone, type.cone)

					Vector(
						sin(yaw) * cos(pitch),
						-sin(pitch),
						cos(yaw) * cos(pitch),
					).rotateAroundY(PI - eye.clone().also { it.y = 0.0 }.angle(direction).toDouble())
				}
				else return@forEach

				ship.sendRichMessage("Before: $eye, After: $dir")

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