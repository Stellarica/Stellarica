package net.stellarica.server.craft.starship.subsystem

import net.stellarica.server.craft.starship.Starship

open class Subsystem(val ship: Starship) {
	open fun onShipPiloted() {}
	open fun onShipUnpiloted() {}
}