package net.stellarica.server.material.custom.block.type

import net.stellarica.server.StellaricaServer.Companion.identifier
import net.stellarica.server.material.custom.block.CustomBlock
import net.stellarica.server.material.custom.item.type.BlockCustomItems
import org.bukkit.Instrument
import org.bukkit.Note

data object MiscCustomBlocks : CustomBlockDef {
	val COMPUTER_CORE: CustomBlock = CustomBlock(
		identifier("computer_core"),
		BlockCustomItems.COMPUTER_CORE,
		note = Note.natural(1, Note.Tone.B),
		instrument = Instrument.PIANO
	)
}