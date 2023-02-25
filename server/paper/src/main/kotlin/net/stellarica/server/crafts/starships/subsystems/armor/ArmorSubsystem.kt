package net.stellarica.server.crafts.starships.subsystems.armor

import net.stellarica.common.utils.OriginRelative
import net.stellarica.server.crafts.starships.Starship
import net.stellarica.server.crafts.starships.subsystems.Subsystem

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
