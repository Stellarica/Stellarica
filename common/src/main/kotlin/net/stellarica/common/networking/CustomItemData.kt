package net.stellarica.common.networking

import kotlinx.serialization.Serializable
import net.minecraft.resources.ResourceLocation
import net.stellarica.common.util.serializer.ResourceLocationSerializer

@Serializable
data class CustomItemData(
        @Serializable(with = ResourceLocationSerializer::class)
        val id: ResourceLocation,
        @Serializable(with = ResourceLocationSerializer::class)
        val base: ResourceLocation,
        val customModelData: Int
)