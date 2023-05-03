package net.stellarica.server.material.custom.item

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Items
import net.stellarica.server.StellaricaServer.Companion.identifier
import net.stellarica.server.material.custom.block.CustomBlocks
import net.stellarica.server.material.type.item.ItemType

@Suppress("Unused") // iea
object CustomItems {

	val DETECTOR: CustomItem = CustomItem(
		identifier("detector"),
		"Detector",
		listOf("Detects multiblocks"),
		ItemType.of(Items.ARROW),
		1,
		null
	)

	val ADAMANTITE_BLOCK: CustomItem = CustomItem(
		identifier("adamantite_block"),
		"Block of Adamantite",
		listOf(),
		ItemType.of(Items.NOTE_BLOCK),
		2,
		null,
		CustomBlocks.ADAMANTITE_BLOCK
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

	val COMPUTER_CORE: CustomItem = CustomItem(
		identifier("computer_core"),
		"Computer Core",
		listOf(),
		ItemType.of(Items.NOTE_BLOCK),
		3,
		null,
		CustomBlocks.COMPUTER_CORE
	)

	val FUEL_JUNCTION: CustomItem = CustomItem(
		identifier("fuel_junction"),
		"Fuel Junction",
		listOf(),
		ItemType.of(Items.NOTE_BLOCK),
		4,
		null,
		CustomBlocks.FUEL_JUNCTION
	)

	val CAPACITOR: CustomItem = CustomItem(
		identifier("capacitor"),
		"Capacitor",
		listOf(),
		ItemType.of(Items.NOTE_BLOCK),
		5,
		null,
		CustomBlocks.CAPACITOR
	)

	val STEEL_FRAME: CustomItem = CustomItem(
		identifier("steel_frame"),
		"Steel Frame",
		listOf(),
		ItemType.of(Items.NOTE_BLOCK),
		6,
		null,
		CustomBlocks.STEEL_FRAME
	)

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

	fun all(): Set<CustomItem> { // can't do lazy{} because reflection
		return this::class.java.declaredFields.mapNotNull { it.get(this) as? CustomItem }.toSet()
	}

	fun byId(id: ResourceLocation): CustomItem? {
		// todo: this could probably be better optimized.
		// maybe keep around a hashmap?
		return all().firstOrNull { it.id == id }
	}
}