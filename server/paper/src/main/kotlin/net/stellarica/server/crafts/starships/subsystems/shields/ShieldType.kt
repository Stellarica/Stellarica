package net.stellarica.server.crafts.starships.subsystems.shields

import net.stellarica.server.multiblocks.MultiblockType
import net.stellarica.server.multiblocks.Multiblocks

enum class ShieldType(val maxHealth: Int, val multiblock: MultiblockType) {
	TEST_SHIELD(400, Multiblocks.TEST_SHIELD);
}