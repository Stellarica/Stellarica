package net.stellarica.server.material.custom.item.type

import net.minecraft.world.item.Items
import net.stellarica.server.StellaricaServer.Companion.identifier
import net.stellarica.server.material.custom.block.type.MiscCustomBlocks
import net.stellarica.server.material.custom.item.CustomItem
import net.stellarica.server.material.type.item.ItemType

data object BlockCustomItems : CustomItemDef {
	val COMPUTER_CORE: CustomItem = CustomItem(
		identifier("computer_core"),
		"Computer Core",
		listOf(),
		ItemType.of(Items.NOTE_BLOCK),
		3,
		null,
		MiscCustomBlocks.COMPUTER_CORE
	)
}