package net.stellarica.server.transfer

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Subcommand
import net.stellarica.server.util.extension.toBlockPos
import org.bukkit.entity.Player

@Suppress("Unused")
@CommandAlias("pipedebug")
@CommandPermission("stellarica.debug.pipe")
class PipeDebugCommands : BaseCommand() {
	@Subcommand("node")
	fun onNode(sender: Player) {
		PipeHandler[sender.world][sender.getTargetBlockExact(10)?.toBlockPos()]?.also {
			sender.sendRichMessage("(${it.pos.x}, ${it.pos.y}, ${it.pos.z})\n" +
					"F: ${it.content.joinToString(", ") { (it as FuelPacket).content.toString() }}\n" +
					"C: ${it.connections.joinToString(", ") { "(${it.x}, ${it.y}, ${it.z})" }}"
			)
		}
	}

	@Subcommand("addFuel")
	fun onAddFuel(sender: Player, fuel: Int) {
		PipeHandler[sender.world][sender.getTargetBlockExact(10)?.toBlockPos()]!!.content.add(FuelPacket(fuel))
	}

	@Subcommand("sum")
	fun onSum(sender: Player) {
		var sum = 0
		var count = 0
		PipeHandler[sender.world].values.forEach { sum += it.content.sumOf { (it as? FuelPacket)?.content ?: 0 }; count++ }
		sender.sendRichMessage("$sum across $count nodes")
	}
}