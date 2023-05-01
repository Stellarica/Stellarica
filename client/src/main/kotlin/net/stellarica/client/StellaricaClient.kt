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
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.stellarica.client.network.ClientboundObjectListener
import net.stellarica.client.network.ClientboundPacketListener
import net.stellarica.client.network.FabricNetworkHandler
import net.stellarica.common.networking.Channel
import net.stellarica.common.networking.CustomItemData
import net.stellarica.common.networking.networkVersion


@Suppress("Unused")
object StellaricaClient : ClientModInitializer {

	lateinit var networkHandler: FabricNetworkHandler

	val klogger = KotlinLogging.logger("Stellarica")

	val itemGroup = FabricItemGroup.builder(identifier("item_group"))
			//.icon()
			.build()

	fun identifier(path: String) = ResourceLocation("stellarica", path)
	override fun onInitializeClient() {
		println("sain and puffering")

		networkHandler = FabricNetworkHandler()

		@Suppress("UnstableApiUsage")
		ItemGroupEvents.modifyEntriesEvent(itemGroup).register(ModifyEntries { content: FabricItemGroupEntries ->

		})

		ClientboundPacketListener(channel = Channel.LOGIN) {
			networkHandler.sendPacket(Channel.LOGIN, byteArrayOf(networkVersion))

			val serverVer = it.first()
			if (networkVersion == serverVer) {
				// success
				Minecraft.getInstance().toasts.addToast(SystemToast(
					SystemToast.SystemToastIds.PERIODIC_NOTIFICATION,
					Component.literal("Stellarica"),
					Component.literal("Connected to server!")
				))
			}
			else if (networkVersion < serverVer) {
				// too old, upgrade
				Minecraft.getInstance().toasts.addToast(SystemToast(
					SystemToast.SystemToastIds.PERIODIC_NOTIFICATION,
					Component.literal("Stellarica"),
					Component.literal("Outdated client mod version! Some features may not work as intended!")
				))
			} else {
				// too new? downgrade??
				Minecraft.getInstance().toasts.addToast(SystemToast(
					SystemToast.SystemToastIds.PERIODIC_NOTIFICATION,
					Component.literal("Stellarica"),
					Component.literal("Outdated server plugin version! Some features may not work as intended!")
				))
			}
			klogger.info { "Connected to Stellarica Server! Server Version: $serverVer, Client Version: $networkVersion" }
			false
		}.register()

		ClientboundObjectListener<List<CustomItemData>>(serializer<List<CustomItemData>>(), channel = Channel.ITEM_SYNC) {data ->
			println("Received custom item data: $data")
			false
		}.register()
	}
}