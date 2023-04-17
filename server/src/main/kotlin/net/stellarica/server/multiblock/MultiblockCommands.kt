package net.stellarica.server.multiblock

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Subcommand
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.stellarica.server.StellaricaServer.Companion.identifier
import net.stellarica.server.multiblock.matching.BlockTagMatcher
import net.stellarica.server.multiblock.matching.MultiBlockMatcher
import net.stellarica.server.multiblock.matching.SingleBlockMatcher
import net.stellarica.server.multiblock.type.Multiblocks
import net.stellarica.server.util.extension.toBlockPos
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("Unused")
@CommandAlias("multiblock|mb")
@CommandPermission("stellarica.debug.multiblock")
class MultiblockCommands : BaseCommand() {

	@Subcommand("type")
	@Description("Get information about a multiblock type")
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
			<dark_gray>${
				"(<gray>${it.key.x}<dark_gray>,<gray> ${it.key.y}<dark_gray>,<gray> ${it.key.z}<dark_gray>)".padEnd(
					61
				)
			} <gray><bold>></bold> <green><bold>${
				when (it.value) {
					is MultiBlockMatcher -> "MULTI  </bold><gray>>\n       - " + (it.value as MultiBlockMatcher).types.joinToString(
						"\n       - "
					) {
						"<gold>${
							it.getId().toString().split(":").let { "<gray>${it.first()}:<gold>${it[1]}" }
						}<gray>,"
					}

					is SingleBlockMatcher -> "SINGLE </bold><gray>> <gold>${
						(it.value as SingleBlockMatcher).block.getId().toString().split(":")
							.let { "<gray>${it.first()}:<gold>${it[1]}" }
					}"

					is BlockTagMatcher -> "TAG    </bold><gray>> <aqua>" + (it.value as BlockTagMatcher).tag.location
					else -> "<red>Unknown Matcher"
				}
			}<reset>
			""".trimIndent()
		})
	}


	@Subcommand("loaded")
	@Description("Dump loaded multiblocks")
	fun onDumpMultiblocks(sender: CommandSender) {
		sender.sendRichMessage(MultiblockHandler.multiblocks.toString())
	}

	@Subcommand("instance")
	@Description("Get multiblock instance")
	fun onInstance(sender: Player) {
		val block = sender.getTargetBlockExact(10)
		val mb = MultiblockHandler[block!!.chunk].firstOrNull { it.contains(block.toBlockPos()) } ?: run {
			sender.sendRichMessage("<red>No multiblock found!")
			return
		}
		sender.sendRichMessage("""
			Type: ${mb.type.displayName}
			Origin: ${mb.origin}
			Direction: ${mb.direction}
			ID: ${mb.id}
			Data: ${Json.encodeToString(mb.data)}
		""".trimIndent())
	}
}

