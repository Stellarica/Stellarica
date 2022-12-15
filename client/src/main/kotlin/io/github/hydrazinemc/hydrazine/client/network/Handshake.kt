package io.github.hydrazinemc.hydrazine.client.network

import io.github.hydrazinemc.hydrazine.client.HydrazineClient
import io.github.hydrazinemc.hydrazine.common.networkVersion
import io.github.hydrazinemc.hydrazine.common.networking.Channel
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.toasts.SystemToast
import net.minecraft.network.chat.Component

class Handshake {
	init {
		ClientPlayConnectionEvents.JOIN.register(ClientPlayConnectionEvents.Join { _, _, _ ->
			println("Sending handshake packet: $networkVersion")
			HydrazineClient.networkHandler.sendPacket(Channel.HANDSHAKE, byteArrayOf(networkVersion))
		})

		HydrazineClient.networkHandler.registerListener(Channel.HANDSHAKE) { bytes ->
			val version = bytes.first()
			println("Received handshake packet: $version")
			if (version == networkVersion) {
				Minecraft.getInstance().toasts.addToast(SystemToast(
					SystemToast.SystemToastIds.PERIODIC_NOTIFICATION,
					Component.literal("Hydrazine"),
					Component.literal("Connected to server! (Version ${bytes.first()})")
				))
			} else if (version > networkVersion) {
				Minecraft.getInstance().toasts.addToast(SystemToast(
					SystemToast.SystemToastIds.WORLD_ACCESS_FAILURE,
					Component.literal("Hydrazine"),
					Component.literal("Outdated client mod version! Some features may not work as intended!")
				))
			}
			else {
				Minecraft.getInstance().toasts.addToast(SystemToast(
					SystemToast.SystemToastIds.WORLD_ACCESS_FAILURE,
					Component.literal("Hydrazine"),
					Component.literal("Outdated server plugin version! Some features may not work as intended!")
				))
			}
		}
	}
}