package net.stellarica.client

import mu.KotlinLogging
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.toasts.SystemToast
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.stellarica.client.feature.Inventory
import net.stellarica.client.network.ClientboundPacketListener
import net.stellarica.client.network.FabricNetworkHandler
import net.stellarica.common.networking.Channel
import net.stellarica.common.networking.networkVersion


@Suppress("Unused")
object StellaricaClient : ClientModInitializer {
	lateinit var networkHandler: FabricNetworkHandler
		private set

	fun identifier(path: String) = ResourceLocation("stellarica", path)

	val klogger = KotlinLogging.logger("Stellarica")

	var connectedToServer = false
		private set

	// The client mod is currently very low-effort, and is mostly just a proof-of-concept.
	// Don't expect sane code or best practices lmao
	// ...not like you'll find those on the server code either, but shush
	// - trainb0y

	override fun onInitializeClient() {
		klogger.info("sain and puffering") // pain and suffering

		networkHandler = FabricNetworkHandler()

		handleOnServerJoin()
		Inventory.setup()
	}

	private fun handleOnServerJoin() = ClientboundPacketListener(channel = Channel.LOGIN) {
		// this is called when the server sends *us* a packet, so we can assume that we're connected to Stellarica
		// specifically, and not just Any Ol' Server:tm:

		networkHandler.sendPacket(Channel.LOGIN, byteArrayOf(networkVersion)) // respond with our version

		val serverVersion = it.first()
		if (networkVersion == serverVersion) connectedToServer = true

		sendLoginToast(serverVersion)

		klogger.info { "Connected to Stellarica Server! Server Version: $serverVersion, Client Version: $networkVersion" }
		false
	}.register()

	private fun sendLoginToast(serverVersion: Byte) {
		Minecraft.getInstance().toasts.addToast(when {
			networkVersion == serverVersion -> SystemToast(
				SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
				Component.literal("Stellarica"),
				Component.literal("Connected to server!")
			)
			networkVersion < serverVersion -> SystemToast(
				SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
				Component.literal("Stellarica"),
				Component.literal("Outdated client mod version! Some features may not work as intended!")
			)
			else -> SystemToast(
				SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
				Component.literal("Stellarica"),
				Component.literal("Outdated server plugin version! Some features may not work as intended!")
			)
		})
	}
}
