package net.stellarica.client

import kotlinx.serialization.serializer
import mu.KotlinLogging
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.ModifyEntries
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.toasts.SystemToast
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.stellarica.client.feature.Inventory
import net.stellarica.client.network.ClientboundObjectListener
import net.stellarica.client.network.ClientboundPacketListener
import net.stellarica.client.network.FabricNetworkHandler
import net.stellarica.common.networking.Channel
import net.stellarica.common.networking.ClientCustomItemData
import net.stellarica.common.networking.networkVersion


@Suppress("Unused")
object StellaricaClient : ClientModInitializer {

	lateinit var networkHandler: FabricNetworkHandler
		private set

	val klogger = KotlinLogging.logger("Stellarica")

	var connectedToServer = false
		private set

	fun identifier(path: String) = ResourceLocation("stellarica", path)
	override fun onInitializeClient() {
		klogger.info("sain and puffering")

		networkHandler = FabricNetworkHandler()

		handleServerJoin()
		Inventory.setup()
	}

	private fun handleServerJoin() = ClientboundPacketListener(channel = Channel.LOGIN) {
		networkHandler.sendPacket(Channel.LOGIN, byteArrayOf(networkVersion))

		val serverVersion = it.first()
		if (networkVersion == serverVersion) {
			connectedToServer = true
		}

		sendLoginToast(serverVersion)

		klogger.info { "Connected to Stellarica Server! Server Version: $serverVersion, Client Version: $networkVersion" }
		false
	}.register()

	private fun sendLoginToast(serverVersion: Byte) {
		if (networkVersion == serverVersion) {
			Minecraft.getInstance().toasts.addToast(
				SystemToast(
					SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
					Component.literal("Stellarica"),
					Component.literal("Connected to server!")
				)
			)
		} else if (networkVersion < serverVersion) {
			Minecraft.getInstance().toasts.addToast(
				SystemToast(
					SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
					Component.literal("Stellarica"),
					Component.literal("Outdated client mod version! Some features may not work as intended!")
				)
			)
		} else {
			Minecraft.getInstance().toasts.addToast(
				SystemToast(
					SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
					Component.literal("Stellarica"),
					Component.literal("Outdated server plugin version! Some features may not work as intended!")
				)
			)
		}
	}
}
