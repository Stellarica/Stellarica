package io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.subsystem.shields;

import io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.Starship
import io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.subsystem.Subsystem
import io.github.hydrazinemc.hydrazine.multiblocks.MultiblockInstance

class ShieldSubsystem(ship: Starship) : Subsystem(ship) {
	val multiblocks = mutableSetOf<MultiblockInstance>()

	var shieldHealth = 0
		private set(value) {
			field = value.coerceIn(0, maxShieldHealth)
		}

	val maxShieldHealth: Int
		get(): Int {
			var h = 0
			multiblocks.forEach { multiblock ->
				ShieldType.values().firstOrNull {
					it.multiblockType == multiblock.type
				}?.let {
					h += it.maxHealth
				}
			}
			return h
		}

	override fun onShipPiloted() {
		ship.multiblocks.forEach { multiblock ->
			if (multiblock.type in ShieldType.values().map{it.multiblockType}) {
				multiblocks.add(multiblock)
			}
		}
	}

	fun damage(dam: Int) {
		// todo: stuff
		shieldHealth -= dam
	}
}
