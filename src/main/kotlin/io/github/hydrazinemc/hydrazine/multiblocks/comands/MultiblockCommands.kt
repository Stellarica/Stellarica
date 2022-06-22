package io.github.hydrazinemc.hydrazine.multiblocks.comands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Subcommand
import io.github.hydrazinemc.hydrazine.multiblocks.multiblocks
import org.bukkit.entity.Player

/**
 * Command handling for the multiblock related commands.
 */
@CommandAlias("multiblock")
class MultiblockCommands : BaseCommand() {

	/**
	 * Get whether the held item is a custom item
	 */
	@Subcommand("debug")
	@Description("Get debug information about a multiblock")
	@CommandPermission("hydrazine.multiblocks.debug.multiblock")
	fun onDebug(sender: Player) {
		val target = sender.getTargetBlock(10) ?: return // this will never happen; it will be a block of air
		val mb = sender.location.chunk.multiblocks.firstOrNull { it.origin == target.location } ?: run {
			sender.sendRichMessage("<gold>No multiblock found at ${target.type}")
			return
		}
		sender.sendRichMessage(
			"""
			<green>---- Multiblock ----
			</green>
			UUID: ${mb.uuid}
			Type: ${mb.name}
			Facing: ${mb.facing}
			
			""".trimIndent()
		)
	}

	/**
	 * Get the multiblocks in a chunk
	 */
	@Subcommand("chunk")
	@Description("Get the multiblocks in the current chunk")
	@CommandPermission("hydrazine.multiblocks.debug.chunk")
	fun onChunk(sender: Player) {
		val mbs = sender.location.chunk.multiblocks
		sender.sendRichMessage(
			"""
			<green>---- Chunk Multiblocks ----
			</green>
			Count: ${mbs.size}
			UUIDs: ${(mbs.map { it.uuid }).joinToString(", ")}
			""".trimIndent()
		)
	}
}

