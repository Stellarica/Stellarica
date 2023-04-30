package net.stellarica.client

import kotlinx.serialization.serializer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.ModifyEntries
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
			println("Received $it from server!")
			networkHandler.sendPacket(Channel.LOGIN, byteArrayOf(networkVersion))
			false
		}.register()

		ClientboundObjectListener<List<CustomItemData>>(serializer<List<CustomItemData>>(), channel = Channel.ITEM_SYNC) {data ->
			println("Received custom item data: $data")
			false
		}.register()
	}
}