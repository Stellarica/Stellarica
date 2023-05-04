package net.stellarica.server.material.custom.item.type

import net.minecraft.world.item.Items
import net.stellarica.server.StellaricaServer.Companion.identifier
import net.stellarica.server.material.custom.item.CustomItem
import net.stellarica.server.material.type.item.ItemType

object MiscCustomItems : CustomItemDef {
	val WIRE: CustomItem = CustomItem(
		identifier("wire"),
		"Wire",
		listOf(),
		ItemType.of(Items.FLINT),
		1
	)

	val PROCESSOR: CustomItem = CustomItem(
		identifier("processor"),
		"Processor",
		listOf(),
		ItemType.of(Items.FLINT),
		2
	)
}