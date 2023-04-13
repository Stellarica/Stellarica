package net.stellarica.server.craft.starship.subsystem.armor

import net.stellarica.common.util.OriginRelative
import net.stellarica.server.craft.starship.Starship
import net.stellarica.server.craft.starship.subsystem.Subsystem

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
