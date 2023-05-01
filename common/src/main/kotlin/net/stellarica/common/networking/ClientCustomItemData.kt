package net.stellarica.common.networking

import kotlinx.serialization.Serializable
import net.minecraft.resources.ResourceLocation
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.ItemStack
import net.stellarica.common.util.serializer.ResourceLocationSerializer

@Serializable
data class ClientCustomItemData(
        @Serializable(with = ResourceLocationSerializer::class)
        val id: ResourceLocation,
        @Serializable(with = ResourceLocationSerializer::class)
        val base: ResourceLocation,
        val customModelData: Int
) {
        fun itemStack(): ItemStack {
                val stack = ItemStack(BuiltInRegistries.ITEM.get(base))
                stack.orCreateTag.putString("client_custom_item", id.toString())
                stack.orCreateTag.putInt("CustomModelData", customModelData)
                return stack
        }
}