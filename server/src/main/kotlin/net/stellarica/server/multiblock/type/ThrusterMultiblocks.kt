package net.stellarica.server.multiblock.type

import net.minecraft.world.level.block.Blocks
import net.stellarica.server.StellaricaServer
import net.stellarica.server.multiblock.MultiblockType

@Suppress("unused")
data object ThrusterMultiblocks : MultiblockDef() {
	val THRUSTER_CHEMICAL_SMALL = object : MultiblockType() {
		override val displayName = "Small Chemical Thruster"
		override val id = StellaricaServer.identifier("thruster_chemical_small")
		override val blocks = mapOf(
			pos(0, 0, 0) match Blocks.IRON_BLOCK,
			pos(0, 0, 1) match Blocks.CUT_COPPER,
			pos(0, 0, 2) match Blocks.IRON_BLOCK,
			pos(0, 0, 3) match Blocks.OCHRE_FROGLIGHT
		)
	}
}
