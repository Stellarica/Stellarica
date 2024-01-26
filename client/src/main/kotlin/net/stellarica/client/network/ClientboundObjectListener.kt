package net.stellarica.client.network

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.cbor.Cbor
import net.stellarica.client.StellaricaClient
import net.stellarica.common.networking.Channel
import net.stellarica.common.networking.NetworkHandler
import net.stellarica.common.networking.PacketListener

@Suppress("UNCHECKED_CAST")
class ClientboundObjectListener<T : Any>(
	val serializer: KSerializer<*>,
	handler: FabricNetworkHandler = StellaricaClient.networkHandler,
	channel: Channel? = null,
	timeout: Long? = null,
	priority: Int = 0,
	val objectCallback: ClientboundObjectListener<T>.(T) -> Boolean
) : ClientboundPacketListener(handler as NetworkHandler<PacketListener>, channel, timeout, priority, ::internal) {
	companion object {
		// todo: cursed, come up with a better solution
		// it does work though, so not super high priority
		@OptIn(ExperimentalSerializationApi::class)
		private fun internal(listener: ClientboundPacketListener, data: ByteArray): Boolean {
			@Suppress("UNCHECKED_CAST")
			listener as ClientboundObjectListener<Any>
			return listener.objectCallback(
				listener,
				Cbor.decodeFromByteArray(listener.serializer, data)!!
			)
		}
	}
}
