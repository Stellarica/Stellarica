package io.github.hydrazinemc.hydrazine.multiblocks.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Subcommand
import io.github.hydrazinemc.hydrazine.utils.OriginRelative
import io.github.hydrazinemc.hydrazine.multiblocks.multiblocks
import org.bukkit.entity.Player

/**
 * Command handling for the multiblock related commands.
 */
@CommandAlias("multiblock")
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
		val mb = sender.location.chunk.multiblocks.firstOrNull { it.origin == target.location } ?: run {
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

	@Subcommand("relative")
	@Description("Get the origin relative position of the block")
	@CommandPermission("hydrazine.multiblocks.debug.multiblock")
	fun onRelative(sender: Player) {
		sender.location.chunk.multiblocks.forEach {
			sender.sendRichMessage(it.getOriginRelative(sender.getTargetBlock(10)!!.location).toString())
		}
	}

	@Subcommand("global")
	@Description("Get the global position of an origin relative position")
	@CommandPermission("hydrazine.multiblocks.debug.multiblock")
	fun onGlobal(sender: Player, x: Int, y: Int, z: Int) {
		val target = sender.getTargetBlock(10) ?: return // this will never happen; it will be a block of air
		val mb = target.chunk.multiblocks.firstOrNull { it.origin == target.location } ?: run {
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
		target.chunk.multiblocks.forEach {
			if (it.contains(target.location)) {
				sender.sendRichMessage("<green>Found ${it.uuid}")
			}
		}
		sender.sendRichMessage("<gray>Checked ${target.location}")
	}
}

