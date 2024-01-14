package net.stellarica.client.network

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.encodeToByteArray
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.stellarica.common.networking.Channel
import net.stellarica.common.networking.NetworkHandler

@OptIn(ExperimentalSerializationApi::class)
class FabricNetworkHandler : NetworkHandler<ClientboundPacketListener> {
	override val listeners = mutableMapOf<ClientboundPacketListener, Long>()

	init {
		for (channel in Channel.entries) {
			ClientPlayNetworking.registerGlobalReceiver(channel.fabric) { _, _, buf, _ ->
				onPacketRecv(channel, buf.array())
			}
		}
	}

	private fun onPacketRecv(channel: Channel, message: ByteArray) {
		val current = System.currentTimeMillis()
		listeners.keys.removeIf { it.timeout != null && listeners[it]!! + it.timeout <= current }

		val toCall = listeners.keys.filter {
			it.channel == channel || it.channel == null
		}.sortedBy { it.priority }
		for (listener in toCall) {
			if (listener.callback(listener, message)) break
		}
	}

	/** Send [content] on [channel] to the server */
	fun sendPacket(channel: Channel, content: ByteArray) {
		ClientPlayNetworking.send(channel.fabric, PacketByteBufs.create().also { it.writeByteArray(content) })
	}

	/** Send [obj] (an object serializable with kotlinx.serialization) on [channel] to the server */
	inline fun <reified T : Any> sendSerializableObject(channel: Channel, obj: T) {
		sendPacket(channel, Cbor.encodeToByteArray(obj))
	}
}