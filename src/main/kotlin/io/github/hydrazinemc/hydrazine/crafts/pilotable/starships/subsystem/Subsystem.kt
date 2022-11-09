package io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.subsystem

import io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.Starship

open class Subsystem(val ship: Starship) {
	open fun onShipPiloted() {}
	open fun onShipUnpiloted() {}
}