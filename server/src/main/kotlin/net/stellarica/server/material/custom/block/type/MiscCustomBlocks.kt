package net.stellarica.server.material.custom.block.type

import net.stellarica.server.StellaricaServer.Companion.identifier
import net.stellarica.server.material.custom.block.CustomBlock
import net.stellarica.server.material.custom.item.type.BlockCustomItems
import org.bukkit.Instrument
import org.bukkit.Note

object MiscCustomBlocks : CustomBlockDef {
	val ADAMANTITE_BLOCK: CustomBlock = CustomBlock(
			identifier("adamantite_block"),
			BlockCustomItems.ADAMANTITE_BLOCK,
			note = Note.natural(1, Note.Tone.A),
			instrument = Instrument.PIANO
	)

	val COMPUTER_CORE: CustomBlock = CustomBlock(
			identifier("computer_core"),
			BlockCustomItems.COMPUTER_CORE,
			note = Note.natural(1, Note.Tone.B),
			instrument = Instrument.PIANO
	)

	val FUEL_JUNCTION: CustomBlock = CustomBlock(
			identifier("fuel_junction"),
			BlockCustomItems.FUEL_JUNCTION,
			note = Note.natural(1, Note.Tone.C),
			instrument = Instrument.PIANO
	)

	val CAPACITOR: CustomBlock = CustomBlock(
			identifier("capacitor"),
			BlockCustomItems.CAPACITOR,
			note = Note.natural(1, Note.Tone.D),
			instrument = Instrument.PIANO
	)

	val STEEL_FRAME: CustomBlock = CustomBlock(
			identifier("steel_frame"),
			BlockCustomItems.STEEL_FRAME,
			note = Note.natural(1, Note.Tone.E),
			instrument = Instrument.PIANO
	)

	val ADAMANTITE_ORE: CustomBlock = CustomBlock(
		identifier("adamantite_ore"),
		BlockCustomItems.ADAMANTITE_ORE,
		note = Note.natural(1, Note.Tone.F),
		instrument = Instrument.PIANO
	)

	val URANIUM_RAW_BLOCK: CustomBlock = CustomBlock(
		identifier("uranium_raw_block"),
		BlockCustomItems.URANIUM_RAW_BLOCK,
		note = Note.natural(1, Note.Tone.G),
		instrument = Instrument.PIANO
	)

	val URANIUM_ORE: CustomBlock = CustomBlock(
		identifier("uranium_ore"),
		BlockCustomItems.URANIUM_ORE,
		note = Note.natural(1, Note.Tone.A),
		instrument = Instrument.PIANO
	)
}