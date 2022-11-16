package io.github.hydrazinemc.hydrazine.utils.extensions

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Subcommand
import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import com.mineinabyss.protocolburrito.dsl.sendTo
import com.mineinabyss.protocolburrito.packets.wrap
import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.plugin
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket
import org.bukkit.block.Block
import org.bukkit.entity.Player

fun Block.setVisualDurability(value: Int) {
	val blockAnim = PacketContainer(PacketType.Play.Server.BLOCK_BREAK_ANIMATION)
	(blockAnim.handle as ClientboundBlockDestructionPacket).wrap().apply {
		id = (Math.random() * 1000).toInt()
		this.pos = this@setVisualDurability.blockLocation.asBlockPos
		progress = value
	}
	plugin.server.onlinePlayers.forEach { blockAnim.sendTo(it) }
}

// todo: delete
@CommandAlias("test")
class TestDebugCommand : BaseCommand() {
	@Subcommand("set")
	fun onSet(sender: Player, value: Int) {
		sender.getTargetBlock(20)?.setVisualDurability(value)
	}
}