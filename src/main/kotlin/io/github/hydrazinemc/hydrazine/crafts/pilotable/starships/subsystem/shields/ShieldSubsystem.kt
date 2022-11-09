package io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.subsystem.shields;

import io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.Starship
import io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.subsystem.Subsystem
import io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.subsystem.weapons.WeaponType
import io.github.hydrazinemc.hydrazine.multiblocks.MultiblockInstance

public class ShieldSubsystem(ship: Starship) : Subsystem(ship) {
	val multiblocks = mutableSetOf<MultiblockInstance>()

	override fun onShipPiloted() {
		ship.multiblocks.forEach { multiblock ->
			if (multiblock.type in ShieldType.values().map{it.multiblockType}) {
				multiblocks.add(multiblock)
			}
		}
	}

	fun fire() {
		multiblocks.filter { it.type == WeaponType.TEST_WEAPON.multiblockType }.forEach {
			WeaponType.TEST_WEAPON.projectile.shoot(it.origin.also { it.direction = ship.pilot!!.eyeLocation.direction })
		}
	}
}
