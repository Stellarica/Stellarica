package io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.subsystem.weapons

import io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.Starship
import io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.subsystem.Subsystem
import io.github.hydrazinemc.hydrazine.multiblocks.MultiblockInstance

class WeaponSubsystem(ship: Starship) : Subsystem(ship) {

	val multiblocks = mutableSetOf<MultiblockInstance>()

	override fun onShipPiloted() {
		ship.multiblocks.forEach {
			if (it.type.name == "testweapon") {
				multiblocks.add(it)
			}
		}
	}

	fun fire() {
		multiblocks.filter { it.type == WeaponType.TEST_WEAPON.multiblockType }.forEach {
			WeaponType.TEST_WEAPON.projectile.shoot(it.origin.also { it.direction = ship.pilot!!.eyeLocation.direction })
		}
	}
}