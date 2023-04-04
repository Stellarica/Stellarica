package net.stellarica.server.crafts.starships.subsystems.shields

import net.stellarica.server.multiblocks.MultiblockType
import net.stellarica.server.multiblocks.Multiblocks

@Suppress("unused")
enum class ShieldType(val maxHealth: Int, val multiblock: MultiblockType) {
	TINY_SHIELD(50, Multiblocks.TINY_SHIELD),
	SMALL_SHIELD(100, Multiblocks.SMALL_SHIELD);
}