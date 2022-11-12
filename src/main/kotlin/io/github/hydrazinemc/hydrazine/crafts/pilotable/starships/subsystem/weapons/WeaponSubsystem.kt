package io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.subsystem.weapons

import io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.Starship
import io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.subsystem.Subsystem
import io.github.hydrazinemc.hydrazine.multiblocks.MultiblockInstance

class WeaponSubsystem(ship: Starship) : Subsystem(ship) {

	val multiblocks = mutableSetOf<MultiblockInstance>()

	override fun onShipPiloted() {
		ship.multiblocks.forEach { multiblock ->
			if (multiblock.type in WeaponType.values().map{it.multiblockType}) {
				multiblocks.add(multiblock)
			}
		}
	}

	override fun onMultiblockUndetected(multiblock: MultiblockInstance) {
		multiblocks.remove(multiblock)
	}

	fun fire() {
		multiblocks.filter { it.type == WeaponType.TEST_WEAPON.multiblockType }.forEach {
			WeaponType.TEST_WEAPON.projectile.shoot(
				ship,
				it.origin.clone().add(0.5, 0.5, 0.5).add(ship.pilot!!.eyeLocation.direction)
					.also { it.direction = ship.pilot!!.eyeLocation.direction })
		}
	}
}