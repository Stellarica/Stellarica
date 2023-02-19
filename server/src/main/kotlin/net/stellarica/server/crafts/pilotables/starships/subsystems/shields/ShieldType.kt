package net.stellarica.server.crafts.pilotables.starships.subsystems.shields

import net.stellarica.server.multiblocks.Multiblocks

enum class ShieldType(val maxHealth: Int, private val multiblockId: String) {
	TEST_SHIELD(100, "test_shield");

	val multiblockType by lazy { // is this even safe, considering multiblock types can be reloaded?
		Multiblocks.types.first { it.name == multiblockId }
	}
}