package net.stellarica.server.networking

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import net.stellarica.common.networking.Channel
import org.bukkit.entity.Player

class ServerboundObjectListener<T : Any>(
	val serializer: KSerializer<*>,
	handler: BukkitNetworkHandler = ModdedPlayerHandler.networkHandler,
	channel: Channel? = null,
	player: Player? = null,
	timeout: Long? = null,
	priority: Int = 0,
	val objectCallback: ServerboundObjectListener<T>.(Player, T) -> Boolean
) : ServerboundPacketListener(handler, channel, player, timeout, priority, ::internal) {
	companion object {
		private fun internal(listener: ServerboundPacketListener, player: Player, data: ByteArray): Boolean {
			@Suppress("UNCHECKED_CAST")
			listener as ServerboundObjectListener<Any>
			return listener.objectCallback(
				listener,
				player,
				Json.decodeFromString(listener.serializer, data.toString())!!
			)
		}
	}
}