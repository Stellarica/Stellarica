package net.stellarica.server.crafts.starships.subsystems.shields

import net.minecraft.resources.ResourceLocation
import net.stellarica.server.StellaricaServer.Companion.identifier
import net.stellarica.server.multiblocks.MultiblockHandler

enum class ShieldType(val maxHealth: Int, private val multiblockId: ResourceLocation) {
	TEST_SHIELD(100, identifier("test_shield"));

	val multiblockType by lazy { // is this even safe, considering multiblock types can be reloaded?
		MultiblockHandler.types.first { it.id == multiblockId }
	}
}