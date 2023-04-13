package net.stellarica.server.material.custom.item

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Items
import net.stellarica.server.StellaricaServer
import net.stellarica.server.material.custom.block.CustomBlocks
import net.stellarica.server.material.type.item.ItemType

@Suppress("Unused") // iea
object CustomItems {

	val DETECTOR: CustomItem = CustomItem(
		StellaricaServer.identifier("detector"),
		"Detector",
		listOf("Detects multiblocks"),
		ItemType.of(Items.ARROW),
		1,
		null
	)

	val ADAMANTITE_BLOCK: CustomItem = CustomItem(
		StellaricaServer.identifier("adamantite_block"),
		"Block of Adamantite",
		listOf(),
		ItemType.of(Items.NOTE_BLOCK),
		2,
		null,
		CustomBlocks.ADAMANTITE_BLOCK
	)

	val TEST_BLASTER: CustomItem = CustomItem(
		StellaricaServer.identifier("test_blaster"),
		"<aqua>Blaster",
		listOf("<dark_gray>pew pew"),
		ItemType.of(Items.GOLDEN_SHOVEL),
		2,
		null,
		maxPower = 100
	)

	fun all(): Set<CustomItem> { // can't do lazy{} because reflection
		return this::class.java.declaredFields.mapNotNull { it.get(this) as? CustomItem }.toSet()
	}

	fun byId(id: ResourceLocation): CustomItem? {
		// todo: this could probably be better optimized.
		// maybe keep around a hashmap?
		return all().firstOrNull { it.id == id }
	}
}