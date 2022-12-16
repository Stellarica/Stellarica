package io.github.hydrazinemc.hydrazine.server.crafts.pilotables.starships.subsystems.armor

import io.github.hydrazinemc.hydrazine.server.crafts.pilotables.starships.Starship
import io.github.hydrazinemc.hydrazine.server.crafts.pilotables.starships.subsystems.Subsystem
import io.github.hydrazinemc.hydrazine.common.utils.OriginRelative

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
