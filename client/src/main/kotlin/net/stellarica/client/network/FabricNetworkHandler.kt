package net.stellarica.client.network

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.stellarica.common.networking.Channel

class FabricNetworkHandler {
	val listeners = mutableSetOf<Pair<Channel, (ByteArray) -> Unit>>()

	fun sendPacket(channel: Channel, content: ByteArray) {
		ClientPlayNetworking.send(channel.fabric, PacketByteBufs.create().also { it.writeByteArray(content) })
	}

	fun registerListener(channel: Channel, listener: (ByteArray) -> Unit) {
		listeners.add(Pair(channel, listener))
	}

	init {
		for (channel in Channel.values()) {
			ClientPlayNetworking.registerGlobalReceiver(channel.fabric) { client, _, buf, _ ->
				listeners.filter { it.first == channel }.forEach { it.second(buf.readByteArray()) }
				println("hey you yes you")
			}
		}
	}
}