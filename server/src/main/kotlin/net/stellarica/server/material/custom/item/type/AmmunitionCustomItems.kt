package net.stellarica.server.material.custom.item.type

import net.minecraft.world.item.Items
import net.stellarica.server.StellaricaServer.Companion.identifier
import net.stellarica.server.material.custom.item.CustomItem
import net.stellarica.server.material.type.item.ItemType

@Suppress("Unused")
object AmmunitionCustomItems : CustomItemDef {
	val AUTO_DRUM: CustomItem = CustomItem(
		identifier("auto_drum"),
		"Auto Drum",
		listOf("Ammuntion, not an instrument lmao"),
		ItemType.of(Items.FLINT),
		3
	)

	val KINETIC_TORPEDO: CustomItem = CustomItem(
		identifier("kinetic_torpedo"),
		"Kinetic Torpedo",
		listOf(),
		ItemType.of(Items.FLINT),
		4
	)

	val NUCLEAR_TORPEDO: CustomItem = CustomItem(
		identifier("nuclear_torpedo"),
		"Nuclear Torpedo",
		listOf(),
		ItemType.of(Items.FLINT),
		5
	)

	val SMALL_RAILGUN_SLUG: CustomItem = CustomItem(
		identifier("small_railgun_slug"),
		"Small Railgun Slug",
		listOf(),
		ItemType.of(Items.FLINT),
		6
	)

	val LARGE_RAILGUN_SLUG: CustomItem = CustomItem(
		identifier("large_railgun_slug"),
		"Large Railgun Slug",
		listOf(),
		ItemType.of(Items.FLINT),
		7
	)
}