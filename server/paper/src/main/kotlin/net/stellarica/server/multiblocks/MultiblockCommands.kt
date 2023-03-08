package net.stellarica.server.multiblocks

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Subcommand
import org.bukkit.command.CommandSender

@Suppress("Unused")
@CommandAlias("multiblock|mb")
class MultiblockCommands : BaseCommand() {

	@Subcommand("types")
	@Description("Dump multiblock types")
	@CommandPermission("stellarica.multiblocks.debug")
	fun onDumpTypes(sender: CommandSender) {
		MultiblockHandler.types.forEach { type ->
			sender.sendRichMessage("")
			sender.sendRichMessage(type.id.toString())
			sender.sendRichMessage(type.blocks.entries.joinToString { "${it.key}: ${it.value}\n" })
		}
	}

	@Subcommand("loaded")
	@Description("Dump loaded multiblocks")
	@CommandPermission("stellarica.multiblocks.debug")
	fun onDumpMultiblocks(sender: CommandSender) {
		sender.sendRichMessage(MultiblockHandler.multiblocks.toString())
	}
}

