package net.stellarica.server.networking

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.cbor.Cbor
import net.stellarica.common.networking.Channel
import net.stellarica.common.networking.NetworkHandler
import net.stellarica.common.networking.PacketListener
import org.bukkit.entity.Player

@Suppress("UNCHECKED_CAST")
class ServerboundObjectListener<T : Any>(
	val serializer: KSerializer<*>,
	handler: BukkitNetworkHandler = ModdedPlayerHandler.networkHandler,
	channel: Channel? = null,
	player: Player? = null,
	timeout: Long? = null,
	priority: Int = 0,
	val objectCallback: ServerboundObjectListener<T>.(Player, T) -> Boolean
) : ServerboundPacketListener(handler as NetworkHandler<PacketListener>, channel, player, timeout, priority, ::internal) {
	companion object {
		@OptIn(ExperimentalSerializationApi::class)
		private fun internal(listener: ServerboundPacketListener, player: Player, data: ByteArray): Boolean {
			@Suppress("UNCHECKED_CAST")
			listener as ServerboundObjectListener<Any>
			return listener.objectCallback(
				listener,
				player,
				Cbor.decodeFromByteArray(listener.serializer, data)!!
			)
		}
	}
}
