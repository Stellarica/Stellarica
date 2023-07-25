package net.stellarica.common.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.resources.ResourceLocation

object ResourceLocationSerializer : KSerializer<ResourceLocation> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ResourceLocation", PrimitiveKind.STRING)

	override fun deserialize(decoder: Decoder): ResourceLocation {
		return ResourceLocation.tryParse(decoder.decodeString())!!
	}

	override fun serialize(encoder: Encoder, value: ResourceLocation) {
		encoder.encodeString(value.toString())
	}
}