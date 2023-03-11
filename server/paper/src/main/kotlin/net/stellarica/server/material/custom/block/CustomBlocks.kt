package net.stellarica.server.material.custom.block

import net.minecraft.resources.ResourceLocation
import net.stellarica.server.StellaricaServer
import net.stellarica.server.material.custom.item.CustomItems
import org.bukkit.Note

object CustomBlocks {
	val TEST_BLOCK = CustomBlock(
		StellaricaServer.identifier("test_block"),
		CustomItems.TEST_ITEM,
		note = Note.flat(1, Note.Tone.C),
		instrument = org.bukkit.Instrument.PIANO
	)

	fun all(): Set<CustomBlock> {
		return this::class.java.declaredFields.mapNotNull { it.get(this) as? CustomBlock }.toSet()
	}

	fun byId(id: ResourceLocation): CustomBlock? {
		// todo: this could probably be better optimized.
		// maybe keep around a hashmap?
		return all().firstOrNull { it.id == id }
	}
}