package net.stellarica.server.networking

import net.stellarica.common.networking.Channel
import net.stellarica.common.networking.NetworkHandler
import net.stellarica.common.networking.PacketListener
import org.bukkit.entity.Player

open class ServerboundPacketListener(
		@Suppress("UNCHECKED_CAST")
		override val handler: NetworkHandler<PacketListener> = ModdedPlayerHandler.networkHandler as NetworkHandler<PacketListener>,
		val channel: Channel? = null,
		val player: Player? = null,
		val timeout: Long? = null,
		val priority: Int = 0,
		val callback: (ServerboundPacketListener.(Player, ByteArray) -> Boolean)
) : PacketListener