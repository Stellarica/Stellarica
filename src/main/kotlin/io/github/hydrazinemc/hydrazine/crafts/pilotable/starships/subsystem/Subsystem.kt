package io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.subsystem

import io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.Starship
import io.github.hydrazinemc.hydrazine.multiblocks.MultiblockInstance

open class Subsystem(val ship: Starship) {
	open fun onShipPiloted() {}
	open fun onShipUnpiloted() {}
	open fun onMultiblockUndetected(multiblock: MultiblockInstance) {}
}