package net.stellarica.client.network

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import net.stellarica.client.StellaricaClient
import net.stellarica.common.networking.Channel

class ClientboundObjectListener<T : Any>(
	val serializer: KSerializer<*>,
	handler: FabricNetworkHandler = StellaricaClient.networkHandler,
	channel: Channel? = null,
	timeout: Long? = null,
	priority: Int = 0,
	val objectCallback: ClientboundObjectListener<T>.(T) -> Boolean
) : ClientboundPacketListener(handler, channel, timeout, priority, ::internal) {
	companion object {
		private fun internal(listener: ClientboundPacketListener, data: ByteArray): Boolean {
			@Suppress("UNCHECKED_CAST")
			listener as ClientboundObjectListener<Any>
			println("Data: " + data.decodeToString())
			return listener.objectCallback(listener, Json.decodeFromString(listener.serializer, data.decodeToString())!!)
		}
	}
}