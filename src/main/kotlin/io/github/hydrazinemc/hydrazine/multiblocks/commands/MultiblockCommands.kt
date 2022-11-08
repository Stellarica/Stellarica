package io.github.hydrazinemc.hydrazine.multiblocks.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Subcommand
import io.github.hydrazinemc.hydrazine.multiblocks.Multiblocks
import io.github.hydrazinemc.hydrazine.utils.OriginRelative
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Command handling for the multiblock related commands.
 */
@CommandAlias("multiblockdebug")
@Suppress("unused")
class MultiblockCommands : BaseCommand() {

	/**
	 * Get whether the held item is a custom item
	 */
	@Subcommand("debug")
	@Description("Get debug information about a multiblock")
	@CommandPermission("hydrazine.multiblocks.debug.multiblock")
	fun onDebug(sender: Player) {
		val target = sender.getTargetBlock(10) ?: return // this will never happen; it will be a block of air
		val mb = Multiblocks.activeMultiblocks.firstOrNull { it.origin == target.location } ?: run {
			sender.sendRichMessage("<gold>No multiblock found at ${target.type}")
			return
		}
		sender.sendRichMessage(
			"""
			<green>---- Multiblock ----
			</green>
			UUID: ${mb.uuid}
			Type: ${mb.type.name}
			Facing: ${mb.facing}
			
			""".trimIndent()
		)
	}

	@Subcommand("relative")
	@Description("Get the origin relative position of the block")
	@CommandPermission("hydrazine.multiblocks.debug.multiblock")
	fun onRelative(sender: Player) {
		Multiblocks.activeMultiblocks.forEach {
			sender.sendRichMessage(it.getOriginRelative(sender.getTargetBlock(10)!!.location).toString())
		}
	}

	@Subcommand("global")
	@Description("Get the global position of an origin relative position")
	@CommandPermission("hydrazine.multiblocks.debug.multiblock")
	fun onGlobal(sender: Player, x: Int, y: Int, z: Int) {
		val target = sender.getTargetBlock(10) ?: return // this will never happen; it will be a block of air
		val mb = Multiblocks.activeMultiblocks.firstOrNull { it.origin == target.location } ?: run {
			sender.sendRichMessage("<gold>No multiblock found at ${target.type}")
			return
		}
		sender.sendRichMessage(mb.getLocation(OriginRelative(x, y, z)).toString())
	}

	@Subcommand("find")
	@Description("Attempt to find a multiblock that contains the block being looked at")
	@CommandPermission("hydrazine.multiblocks.debug.multiblock")
	fun onFind(sender: Player) {
		val target = sender.getTargetBlock(10) ?: return // this will never happen; it will be a block of air
		Multiblocks.activeMultiblocks.forEach {
			if (it.contains(target.location)) {
				sender.sendRichMessage("<green>Found ${it.uuid}")
			}
		}
		sender.sendRichMessage("<gray>Checked ${target.location}")
	}

	@Subcommand("active")
	@Description("List the active multiblocks")
	@CommandPermission("hydrazine.multiblocks.debug.multiblock")
	fun onListActive(sender: CommandSender) {
		sender.sendMessage(Multiblocks.activeMultiblocks.joinToString("\n") { it.uuid.toString() })
	}
}

