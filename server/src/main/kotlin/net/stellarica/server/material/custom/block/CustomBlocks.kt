package net.stellarica.server.material.custom.block

import net.minecraft.resources.ResourceLocation
import net.stellarica.server.StellaricaServer
import net.stellarica.server.material.custom.item.CustomItems
import org.bukkit.Note

@Suppress("unused")
object CustomBlocks {
	val TEST_BLOCK: CustomBlock = CustomBlock(
		StellaricaServer.identifier("test_block"),
		CustomItems.TEST_BLOCK,
		note = Note.natural(1, Note.Tone.C),
		instrument = org.bukkit.Instrument.PIANO
	)

	val ADAMANTITE_BLOCK: CustomBlock = CustomBlock(
		StellaricaServer.identifier("adamantite_block"),
		CustomItems.ADAMANTITE_BLOCK,
		note = Note.natural(1, Note.Tone.D),
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