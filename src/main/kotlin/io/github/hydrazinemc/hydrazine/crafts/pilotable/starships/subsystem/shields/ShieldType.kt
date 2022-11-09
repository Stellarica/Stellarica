package io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.subsystem.shields

import io.github.hydrazinemc.hydrazine.multiblocks.Multiblocks

enum class ShieldType(private val multiblockId: String) {
	TEST_SHIELD("test_shield");

	val multiblockType by lazy { // is this even safe, considering multiblock types can be reloaded?
		Multiblocks.types.first { it.name == multiblockId }
	}
}