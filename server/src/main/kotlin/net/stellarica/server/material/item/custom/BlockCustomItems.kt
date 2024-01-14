package net.stellarica.server.material.item.custom

import net.minecraft.world.item.Items
import net.stellarica.server.StellaricaServer.Companion.identifier
import net.stellarica.server.material.block.custom.MiscCustomBlocks
import net.stellarica.server.material.item.CustomItem
import net.stellarica.server.material.item.type.ItemType

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