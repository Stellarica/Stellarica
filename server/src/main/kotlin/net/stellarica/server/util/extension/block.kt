package net.stellarica.server.util.extension

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import com.mineinabyss.protocolburrito.dsl.sendTo
import com.mineinabyss.protocolburrito.packets.wrap
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket
import net.stellarica.common.util.toBlockPos
import net.stellarica.server.StellaricaServer.Companion.plugin

fun org.bukkit.block.Block.setVisualDurability(value: Int) {
	val blockAnim = PacketContainer(PacketType.Play.Server.BLOCK_BREAK_ANIMATION)
	(blockAnim.handle as ClientboundBlockDestructionPacket).wrap().apply {
		@Suppress("DEPRECATION")
		id = this@setVisualDurability.blockKey.toInt() // not perfect, good enough though
		this.pos = this@setVisualDurability.toBlockPosition().toBlockPos()
		progress = value
	}
	plugin.server.onlinePlayers.forEach { blockAnim.sendTo(it) }
}