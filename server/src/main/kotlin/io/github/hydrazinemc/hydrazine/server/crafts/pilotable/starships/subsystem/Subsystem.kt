package io.github.hydrazinemc.hydrazine.server.crafts.pilotable.starships.subsystem

import io.github.hydrazinemc.hydrazine.server.crafts.pilotable.starships.Starship
import io.github.hydrazinemc.hydrazine.server.multiblocks.MultiblockInstance

open class Subsystem(val ship: Starship) {
	open fun onShipPiloted() {}
	open fun onShipUnpiloted() {}
	open fun onMultiblockUndetected(multiblock: MultiblockInstance) {}
}

// todo: clean up weak refrences for subsystem multiblocks