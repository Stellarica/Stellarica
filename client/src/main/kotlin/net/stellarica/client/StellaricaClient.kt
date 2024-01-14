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

	private val itemGroup: ResourceKey<CreativeModeTab> = ResourceKey.create(Registries.CREATIVE_MODE_TAB, identifier("item_group"))

	val customItems = mutableSetOf<ClientCustomItemData>()

	var connectedToServer = false
		private set

	fun identifier(path: String) = ResourceLocation("stellarica", path)
	override fun onInitializeClient() {
		println("sain and puffering")

		networkHandler = FabricNetworkHandler()


		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, itemGroup, FabricItemGroup.builder()
				.icon {
					ItemStack(Items.FLINT).also {
						it.orCreateTag.putInt("CustomModelData", 2)
					}
				}
				.title(Component.literal("Stellarica"))
				.build()
		)

		handleServerJoin()
		handleCreativeMenu()
	}

	private fun handleServerJoin() = ClientboundPacketListener(channel = Channel.LOGIN) {
		networkHandler.sendPacket(Channel.LOGIN, byteArrayOf(networkVersion))

		val serverVer = it.first()
		if (networkVersion == serverVer) {
			// success
			Minecraft.getInstance().toasts.addToast(
					SystemToast(
							SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
							Component.literal("Stellarica"),
							Component.literal("Connected to server!")
					)
			)
			connectedToServer = true
		} else if (networkVersion < serverVer) {
			// too old, upgrade
			Minecraft.getInstance().toasts.addToast(
					SystemToast(
							SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
							Component.literal("Stellarica"),
							Component.literal("Outdated client mod version! Some features may not work as intended!")
					)
			)
		} else {
			// too new? downgrade??
			Minecraft.getInstance().toasts.addToast(
					SystemToast(
							SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
							Component.literal("Stellarica"),
							Component.literal("Outdated server plugin version! Some features may not work as intended!")
					)
			)
		}
		klogger.info { "Connected to Stellarica Server! Server Version: $serverVer, Client Version: $networkVersion" }
		false
	}.register()

	private fun handleCreativeMenu() {
		ClientboundObjectListener<List<ClientCustomItemData>>(
				serializer<List<ClientCustomItemData>>(),
				channel = Channel.ITEM_SYNC
		) { data ->
			customItems.clear()
			customItems.addAll(data)
			false
		}.register()

		@Suppress("UnstableApiUsage")
		ItemGroupEvents.modifyEntriesEvent(itemGroup).register(ModifyEntries { content: FabricItemGroupEntries ->
			content.acceptAll(customItems.map { it.itemStack() })
		})
	}
}