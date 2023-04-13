package net.stellarica.server.craft.starship.subsystem.shield

import net.stellarica.server.multiblock.MultiblockType
import net.stellarica.server.multiblock.Multiblocks

@Suppress("unused")
enum class ShieldType(val maxHealth: Int, val multiblock: MultiblockType) {
	TINY_SHIELD(50, Multiblocks.TINY_SHIELD),
	SMALL_SHIELD(100, Multiblocks.SMALL_SHIELD);
}