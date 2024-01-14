package net.stellarica.server.material.item.custom

import net.minecraft.world.item.Items
import net.stellarica.server.StellaricaServer.Companion.identifier
import net.stellarica.server.material.item.CustomItem
import net.stellarica.server.material.item.type.ItemType

data object DebugCustomItems : CustomItemDef {
	val DETECTOR: CustomItem = CustomItem(
			identifier("detector"),
			"Detector",
			listOf("Detects multiblocks"),
			ItemType.of(Items.ARROW),
			1,
			null
	)

	val TEST_BLASTER: CustomItem = CustomItem(
			identifier("test_blaster"),
			"<aqua>Blaster <i>(debug)</i>",
			listOf("<dark_gray>pew pew"),
			ItemType.of(Items.GOLDEN_SHOVEL),
			2,
			null,
			maxPower = 100
	)
}