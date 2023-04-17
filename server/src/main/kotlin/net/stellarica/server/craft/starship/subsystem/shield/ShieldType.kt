package net.stellarica.server.craft.starship.subsystem.shield

import net.stellarica.server.multiblock.MultiblockType
import net.stellarica.server.multiblock.type.ShieldMultiblocks

@Suppress("unused")
enum class ShieldType(val maxHealth: Int, val multiblock: MultiblockType) {
	TINY_SHIELD(50, ShieldMultiblocks.TINY_SHIELD),
	SMALL_SHIELD(100, ShieldMultiblocks.SMALL_SHIELD);
}