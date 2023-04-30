package net.stellarica.server.craft.starship.subsystem.thruster

import net.stellarica.server.multiblock.MultiblockType
import net.stellarica.server.multiblock.type.ThrusterMultiblocks

enum class ThrusterType(
	val multiblock: MultiblockType,
	val maxThrust: Int,
	val warmupSpeed: Int,
) {
	CHEMICAL_SMALL(
		ThrusterMultiblocks.THRUSTER_CHEMICAL_SMALL,
		500,
		5
	)
}