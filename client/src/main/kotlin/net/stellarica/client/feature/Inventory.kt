package net.stellarica.client.feature

import kotlinx.serialization.serializer
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.stellarica.client.StellaricaClient
import net.stellarica.client.network.ClientboundObjectListener
import net.stellarica.common.networking.Channel
import net.stellarica.common.networking.ClientCustomItemData

object Inventory {
	private val itemGroup = ResourceKey.create(Registries.CREATIVE_MODE_TAB, StellaricaClient.identifier("item_group"))
	private val customItems = mutableSetOf<ClientCustomItemData>()

	fun setup() {
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, itemGroup, FabricItemGroup.builder()
			.icon { ItemStack(Items.ENDER_EYE) }
			.title(Component.literal("Stellarica"))
			.build()
		)

		ClientboundObjectListener<List<ClientCustomItemData>>(
			serializer<List<ClientCustomItemData>>(),
			channel = Channel.ITEM_SYNC
		) { data ->
			customItems.clear()
			customItems.addAll(data)
			false
		}.register()

		ItemGroupEvents.modifyEntriesEvent(itemGroup).register(ItemGroupEvents.ModifyEntries { content: FabricItemGroupEntries ->
			content.acceptAll(customItems.map { it.itemStack() })
		})
	}
}
