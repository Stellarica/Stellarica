package net.stellarica.client.network

import net.stellarica.client.StellaricaClient
import net.stellarica.common.networking.Channel

open class ClientboundPacketListener(
	val handler: FabricNetworkHandler = StellaricaClient.networkHandler,
	val channel: Channel? = null,
	val timeout: Long? = null,
	val priority: Int = 0,
	val callback: (ClientboundPacketListener.(ByteArray) -> Boolean)
) {
	fun unregister() {
		handler.unregister(this)
	}

	fun register() {
		handler.register(this)
	}
}