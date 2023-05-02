package net.stellarica.server.material.custom.block

import net.minecraft.resources.ResourceLocation
import net.stellarica.server.StellaricaServer.Companion.identifier
import net.stellarica.server.material.custom.item.CustomItems
import org.bukkit.Note

@Suppress("unused")
object CustomBlocks {
	val ADAMANTITE_BLOCK: CustomBlock = CustomBlock(
		identifier("adamantite_block"),
		CustomItems.ADAMANTITE_BLOCK,
		note = Note.natural(1, Note.Tone.A),
		instrument = org.bukkit.Instrument.PIANO
	)

	val COMPUTER_CORE: CustomBlock = CustomBlock(
		identifier("computer_core"),
		CustomItems.COMPUTER_CORE,
		note = Note.natural(1, Note.Tone.B),
		instrument = org.bukkit.Instrument.PIANO
	)

	val FUEL_JUNCTION: CustomBlock = CustomBlock(
		identifier("fuel_junction"),
		CustomItems.FUEL_JUNCTION,
		note = Note.natural(1, Note.Tone.C),
		instrument = org.bukkit.Instrument.PIANO
	)

	val CAPACITOR: CustomBlock = CustomBlock(
		identifier("capacitor"),
		CustomItems.CAPACITOR,
		note = Note.natural(1, Note.Tone.D),
		instrument = org.bukkit.Instrument.PIANO
	)

	val STEEL_FRAME: CustomBlock = CustomBlock(
		identifier("steel_frame"),
		CustomItems.STEEL_FRAME,
		note = Note.natural(1, Note.Tone.E),
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