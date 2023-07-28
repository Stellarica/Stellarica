package net.stellarica.server.networking

import net.stellarica.common.networking.Channel
import org.bukkit.entity.Player

open class ServerboundPacketListener(
	val handler: BukkitNetworkHandler = ModdedPlayerHandler.networkHandler,
	val channel: Channel? = null,
	val player: Player? = null,
	val timeout: Long? = null,
	val priority: Int = 0,
	val callback: (ServerboundPacketListener.(Player, ByteArray) -> Boolean)
) {
	fun unregister() {
		handler.unregister(this)
	}

	fun register() {
		handler.register(this)
	}
}