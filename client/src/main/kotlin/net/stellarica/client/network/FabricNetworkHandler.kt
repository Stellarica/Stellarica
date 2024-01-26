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

	private fun onPacketRecv(channel: Channel, message: ByteArray) = listeners.keys
		// c h a i n s
		.also { keys ->
			val current = System.currentTimeMillis()
			keys.removeIf { it.timeout != null && listeners[it]!! + it.timeout <= current }
		}
		.filter { it.channel == channel || it.channel == null }
		.sortedBy { it.priority }
		.forEach { if (it.callback(it, message)) return }


	/** Sends [content] on [channel] to the server plugin */
	fun sendPacket(channel: Channel, content: ByteArray) {
		ClientPlayNetworking.send(channel.fabric, PacketByteBufs.create().also { it.writeByteArray(content) })
	}

	/** Sends [obj] (an object serializable with kotlinx.serialization) on [channel] to the server plugin */
	inline fun <reified T : Any> sendSerializableObject(channel: Channel, obj: T) {
		sendPacket(channel, Cbor.encodeToByteArray(obj))
	}
}
