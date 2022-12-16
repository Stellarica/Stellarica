package io.github.hydrazinemc.hydrazine.server.crafts.pilotables.starships.subsystems.weapons

import io.github.hydrazinemc.hydrazine.server.crafts.pilotables.starships.Starship
import io.github.hydrazinemc.hydrazine.server.crafts.pilotables.starships.subsystems.Subsystem
import io.github.hydrazinemc.hydrazine.server.multiblocks.MultiblockInstance
import io.github.hydrazinemc.hydrazine.server.utils.extensions.sendRichMessage
import java.lang.ref.WeakReference

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
		val eye = ship.pilot!!.eyeLocation.direction.clone()

		WeaponType.values().sortedBy{it.priority}.forEach {type ->
			multiblocks.mapNotNull{it.get()}.filter{ it.type == type.multiblockType }.forEach { multiblock ->
				ship.sendRichMessage(eye.angle(multiblock.getLocation(type.direction).clone().subtract(multiblock.getLocation(type.mount)).toVector()).toString())

				type.projectile.shoot(
					ship,
					multiblock.getLocation(type.mount).clone().add(0.5, 0.5, 0.5).add(eye)
						.also { it.direction = ship.pilot!!.eyeLocation.direction }
				)
			}
		}
	}
}