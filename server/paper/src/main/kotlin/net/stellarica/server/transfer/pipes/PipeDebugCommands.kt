package net.stellarica.server.transfer.pipes

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Subcommand
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
		val net = PipeNetwork(b, (sender.world as CraftWorld).handle)
		net.detect()
		pipes[sender] = net
	}

	@Subcommand("tick")
	fun onTick(sender: Player) {
		pipes[sender]?.tick()
	}

	@Subcommand("remove")
	fun onRemove(sender: Player) {
		pipes.remove(sender)
	}

	@Subcommand("info")
	fun onInfo(sender: Player) {
		val n = pipes[sender] ?: return
		println(n.graph)
	}
}