package net.stellarica.server.crafts.pilotables.starships.subsystems.armor

import net.stellarica.server.crafts.pilotables.starships.Starship
import net.stellarica.server.crafts.pilotables.starships.subsystems.Subsystem
import net.stellarica.common.utils.OriginRelative

class ArmorSubsystem(ship: Starship) : Subsystem(ship) {
	var armor = mutableMapOf<OriginRelative, Float>()
	/*
override fun onShipPiloted() {
	ship.detectedBlocks.forEach { loc ->
		armor[OriginRelative(loc)] = ArmorValues[loc.bukkit.type]
	}
}
	*/
}
