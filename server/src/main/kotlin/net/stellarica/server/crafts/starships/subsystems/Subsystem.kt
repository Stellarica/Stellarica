package net.stellarica.server.crafts.starships.subsystems

import net.stellarica.server.crafts.starships.Starship

open class Subsystem(val ship: Starship) {
	open fun onShipPiloted() {}
	open fun onShipUnpiloted() {}
}