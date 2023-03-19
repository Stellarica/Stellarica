package net.stellarica.server.multiblocks

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Subcommand
import net.stellarica.server.StellaricaServer.Companion.identifier
import net.stellarica.server.multiblocks.matching.BlockTagMatcher
import net.stellarica.server.multiblocks.matching.MultiBlockMatcher
import net.stellarica.server.multiblocks.matching.SingleBlockMatcher
import org.bukkit.command.CommandSender

@Suppress("Unused")
@CommandAlias("multiblock|mb")
class MultiblockCommands : BaseCommand() {

	@Subcommand("types")
	@Description("Dump multiblock types")
	@CommandPermission("stellarica.multiblocks.debug")
	@CommandCompletion("@multiblocks")
	fun onDumpType(sender: CommandSender, id: String) {
		val type = Multiblocks.byId(identifier(id)) ?: run {
			sender.sendRichMessage("<red>No multiblock with the id '$id' found.")
			return
		}
		sender.sendRichMessage("<bold><gold>Multiblock:</bold> ${type.id}")

		// Do you have string formatting nightmares?
		// If you didn't, now you will
		// :help_me:
		sender.sendRichMessage(type.blocks.entries.joinToString("\n") {
			"""
			<dark_gray>${"(<gray>${it.key.x}<dark_gray>,<gray> ${it.key.y}<dark_gray>,<gray> ${it.key.z}<dark_gray>)".padEnd(61)} <gray><bold>></bold> <green><bold>${
				when (it.value) {
					is MultiBlockMatcher -> "MULTI  </bold><gray>>\n       - " + (it.value as MultiBlockMatcher).types.joinToString("\n       - ") { "<gold>${it.getId().toString().split(":").let { "<gray>${it.first()}:<gold>${it[1]}"}}<gray>," }

					is SingleBlockMatcher -> "SINGLE </bold><gray>> <gold>${(it.value as SingleBlockMatcher).block.getId().toString().split(":").let { "<gray>${it.first()}:<gold>${it[1]}"}}"
					is BlockTagMatcher -> "TAG    </bold><gray>> <aqua>" + (it.value as BlockTagMatcher).tag.location
					else -> "<red>Unknown Matcher"
				}
			}<reset>
			""".trimIndent()
		})
	}


	@Subcommand("loaded")
	@Description("Dump loaded multiblocks")
	@CommandPermission("stellarica.multiblocks.debug")
	fun onDumpMultiblocks(sender: CommandSender) {
		sender.sendRichMessage(MultiblockHandler.multiblocks.toString())
	}
}

