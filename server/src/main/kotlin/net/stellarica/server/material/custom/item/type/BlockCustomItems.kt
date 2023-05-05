package net.stellarica.server.material.custom.item.type

import net.minecraft.world.item.Items
import net.stellarica.server.StellaricaServer.Companion.identifier
import net.stellarica.server.material.custom.block.type.MiscCustomBlocks
import net.stellarica.server.material.custom.item.CustomItem
import net.stellarica.server.material.type.item.ItemType

object BlockCustomItems : CustomItemDef {
	val ADAMANTITE_BLOCK: CustomItem = CustomItem(
			identifier("adamantite_block"),
			"Block of Adamantite",
			listOf(),
			ItemType.of(Items.NOTE_BLOCK),
			2,
			null,
			MiscCustomBlocks.ADAMANTITE_BLOCK
	)

	val COMPUTER_CORE: CustomItem = CustomItem(
			identifier("computer_core"),
			"Computer Core",
			listOf(),
			ItemType.of(Items.NOTE_BLOCK),
			3,
			null,
			MiscCustomBlocks.COMPUTER_CORE
	)

	val FUEL_JUNCTION: CustomItem = CustomItem(
			identifier("fuel_junction"),
			"Fuel Junction",
			listOf(),
			ItemType.of(Items.NOTE_BLOCK),
			4,
			null,
			MiscCustomBlocks.FUEL_JUNCTION
	)

	val CAPACITOR: CustomItem = CustomItem(
			identifier("capacitor"),
			"Capacitor",
			listOf(),
			ItemType.of(Items.NOTE_BLOCK),
			5,
			null,
			MiscCustomBlocks.CAPACITOR
	)

	val STEEL_FRAME: CustomItem = CustomItem(
			identifier("steel_frame"),
			"Steel Frame",
			listOf(),
			ItemType.of(Items.NOTE_BLOCK),
			6,
			null,
			MiscCustomBlocks.STEEL_FRAME
	)
}