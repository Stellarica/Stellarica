package net.stellarica.server.networking

import net.stellarica.common.networking.Channel
import net.stellarica.common.networking.NetworkHandler
import net.stellarica.common.networking.PacketListener
import org.bukkit.entity.Player

open class ServerboundPacketListener(
	override val handler = ModdedPlayerHandler.networkHandler,
	val channel: Channel? = null,
	val player: Player? = null,
	val timeout: Long? = null,
	val priority: Int = 0,
	val callback: (ServerboundPacketListener.(Player, ByteArray) -> Boolean)
) : PacketListener