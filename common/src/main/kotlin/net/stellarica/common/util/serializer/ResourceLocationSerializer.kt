package net.stellarica.common.util.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.CharArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.resources.ResourceLocation

object ResourceLocationSerializer : KSerializer<ResourceLocation> {
	@OptIn(ExperimentalSerializationApi::class)
	override val descriptor: SerialDescriptor = SerialDescriptor("ResourceLocation", CharArraySerializer().descriptor)

	override fun deserialize(decoder: Decoder): ResourceLocation {
		val array = decoder.decodeSerializableValue(CharArraySerializer())
		return ResourceLocation.tryParse(array.concatToString())!!
	}

	override fun serialize(encoder: Encoder, value: ResourceLocation) {
		encoder.encodeSerializableValue(CharArraySerializer(), value.toString().toCharArray())
	}
}