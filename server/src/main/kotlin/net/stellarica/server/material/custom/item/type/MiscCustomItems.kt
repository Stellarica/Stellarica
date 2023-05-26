package net.stellarica.server.material.custom.item.type

import net.minecraft.world.item.Items
import net.stellarica.server.StellaricaServer.Companion.identifier
import net.stellarica.server.material.custom.item.CustomItem
import net.stellarica.server.material.type.item.ItemType

@Suppress("Unused")
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

	val URANIUM_RAW: CustomItem = CustomItem(
		identifier("uranium_raw"),
		"Raw Uranium",
		listOf(),
		ItemType.of(Items.FLINT),
		8
	)

	val URANIUM_INGOT: CustomItem = CustomItem(
		identifier("uranium_ingot"),
		"Uranium Ingot",
		listOf(),
		ItemType.of(Items.FLINT),
		9
	)

	val ADAMANTITE_INGOT: CustomItem = CustomItem(
		identifier("adamantite_ingot"),
		"Adamantite Ingot",
		listOf(),
		ItemType.of(Items.FLINT),
		10
	)

	val JETPACK: CustomItem = CustomItem(
		identifier("jetpack"),
		"Jetpack",
		listOf("haha go brrr"),
		ItemType.of(Items.LEATHER_CHESTPLATE),
		1,
		maxPower = 1000
	)
}