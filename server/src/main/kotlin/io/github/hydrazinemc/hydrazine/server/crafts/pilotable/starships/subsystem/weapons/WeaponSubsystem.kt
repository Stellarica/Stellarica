package io.github.hydrazinemc.hydrazine.server.crafts.pilotable.starships.subsystem.weapons

import io.github.hydrazinemc.hydrazine.server.crafts.pilotable.starships.Starship
import io.github.hydrazinemc.hydrazine.server.crafts.pilotable.starships.subsystem.Subsystem
import io.github.hydrazinemc.hydrazine.server.multiblocks.MultiblockInstance
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
		multiblocks.filter { it.get()?.type == WeaponType.TEST_WEAPON.multiblockType }.forEach { ref ->
			WeaponType.TEST_WEAPON.projectile.shoot(
				ship,
				ref.get()!!.origin.clone().add(0.5, 0.5, 0.5).add(ship.pilot!!.eyeLocation.direction)
					.also { it.direction = ship.pilot!!.eyeLocation.direction })
		}
	}
}