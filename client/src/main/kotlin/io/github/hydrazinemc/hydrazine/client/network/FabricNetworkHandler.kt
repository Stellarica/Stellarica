package io.github.hydrazinemc.hydrazine.client.network

import io.github.hydrazinemc.hydrazine.common.networking.Channel
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs

class FabricNetworkHandler {
	val listeners = mutableSetOf<Pair<Channel, (ByteArray) -> Unit>>()

	fun sendPacket(channel: Channel, content: ByteArray) {
		ClientPlayNetworking.send(channel.fabric, PacketByteBufs.create().also { it.writeByteArray(content) })
	}

	fun registerListener(channel: Channel, listener: (ByteArray) -> Unit) {
		listeners.add(Pair(channel, listener))
	}

	init {
		Channel.values().forEach { channel ->
			ClientPlayNetworking.registerGlobalReceiver(channel.fabric) { _, _, buf, _ ->
				listeners.filter { it.first == channel }.forEach { it.second(buf.readByteArray()) }
			}
		}
	}
}