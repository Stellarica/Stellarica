package net.stellarica.common.networking

import kotlinx.serialization.Serializable
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.stellarica.common.serializer.ResourceLocationSerializer

@Serializable
data class ClientCustomItemData(
	@Serializable(with = ResourceLocationSerializer::class)
	val id: ResourceLocation,
	@Serializable(with = ResourceLocationSerializer::class)
	val base: ResourceLocation,
	val customModelData: Int,
	val displayNameJson: String
) {
	// I really don't like that this is a completely separate class from CustomItem
	// todo: why aren't we just serializing and sending the normal server-side custom item data?
	fun itemStack(): ItemStack {
		val stack = ItemStack(BuiltInRegistries.ITEM.get(base))
		stack.orCreateTag.putString("client_custom_item", id.toString())
		stack.orCreateTag.putInt("CustomModelData", customModelData)
		stack.hoverName = Component.Serializer.fromJson(displayNameJson)!!.withStyle(Style.EMPTY.withItalic(false))
		return stack
	}
}
