package net.stellarica.server.command

import cloud.commandframework.annotations.CommandMethod
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.stellarica.server.multiblock.MultiblockHandler
import org.bukkit.command.CommandSender

@Suppress("unused")
object MultiblockCommand {
	private val json = Json { prettyPrint = true }

	@CommandMethod("multiblock dump")
	fun dump(sender: CommandSender) {
		sender.sendRichMessage("\n" + json.encodeToString(MultiblockHandler.getAllLoaded()))
	}
}
