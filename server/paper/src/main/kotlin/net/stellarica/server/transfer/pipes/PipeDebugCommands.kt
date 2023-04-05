package net.stellarica.server.transfer.pipes

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Subcommand
import net.stellarica.common.utils.OriginRelative
import net.stellarica.server.utils.extensions.toBlockPos
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld
import org.bukkit.entity.Player

@Suppress("Unused")
@CommandAlias("pipedebug")
class PipeDebugCommands : BaseCommand() {
	val pipes = mutableMapOf<Player, PipeNetwork>() // testing only

	@Subcommand("detect")
	fun onDetect(sender: Player) {
		val b = sender.getTargetBlockExact(10)?.toBlockPos() ?: return
		pipes[sender] = PipeHandler.detectPipeNetwork(b, (sender.world as CraftWorld).handle)!!
	}

	@Subcommand("tick")
	fun onTick(sender: Player) {
		pipes[sender]?.tick()
	}

	@Subcommand("remove")
	fun onRemove(sender: Player) {
		pipes.remove(sender)
	}

	@Subcommand("getNode")
	fun onGetNode(sender: Player) {
		val b = sender.getTargetBlockExact(10)?.toBlockPos() ?: return
		val net = pipes[sender] ?: return
		val rel = OriginRelative.getOriginRelative(b, net.origin, net.direction)
		net.nodes[rel]?.let {
			sender.sendRichMessage(it.toString())
		}
	}
}