package net.stellarica.client.network

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.stellarica.common.networking.Channel

class FabricNetworkHandler {
	private val listeners = mutableMapOf<ClientboundPacketListener, Long>()

	fun sendPacket(channel: Channel, content: ByteArray) {
		ClientPlayNetworking.send(channel.fabric, PacketByteBufs.create().also { it.writeByteArray(content) })
	}

	fun register(listener: ClientboundPacketListener) {
		listeners[listener] = System.currentTimeMillis()
	}

	fun unregister(listener: ClientboundPacketListener) {
		listeners.remove(listener)
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


	init {
		for (channel in Channel.values()) {
			ClientPlayNetworking.registerGlobalReceiver(channel.fabric) { _, _, buf, _ ->
				onPacketRecv(channel, buf.accessByteBufWithCorrectSize())
			}
		}
	}
}