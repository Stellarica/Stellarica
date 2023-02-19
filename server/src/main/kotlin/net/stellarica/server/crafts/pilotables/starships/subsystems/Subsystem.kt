package net.stellarica.server.crafts.pilotables.starships.subsystems

import net.stellarica.server.crafts.pilotables.starships.Starship
import net.stellarica.server.multiblocks.MultiblockInstance

open class Subsystem(val ship: Starship) {
	open fun onShipPiloted() {}
	open fun onShipUnpiloted() {}
}

// todo: clean up weak refrences for subsystems multiblocks and starships