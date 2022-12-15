package io.github.hydrazinemc.hydrazine.client.network

import io.github.hydrazinemc.hydrazine.client.HydrazineClient
import io.github.hydrazinemc.hydrazine.common.networkVersion
import io.github.hydrazinemc.hydrazine.common.networking.Channel
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents

class Handshake {
	init {
		ClientPlayConnectionEvents.JOIN.register(ClientPlayConnectionEvents.Join { _, _, _ ->
			println("Sending handshake packet: $networkVersion")
			HydrazineClient.networkHandler.sendPacket(Channel.HANDSHAKE, byteArrayOf(networkVersion))
		})

		HydrazineClient.networkHandler.registerListener(Channel.HANDSHAKE) { bytes ->
			println("Received handshake packet: ${bytes.contentToString()}")
		}
	}
}