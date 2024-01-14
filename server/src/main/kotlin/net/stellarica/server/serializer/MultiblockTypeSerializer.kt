package net.stellarica.server.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.resources.ResourceLocation
import net.stellarica.server.Multiblocks
import net.stellarica.server.multiblock.MultiblockType

object MultiblockTypeSerializer : KSerializer<MultiblockType> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("type", PrimitiveKind.STRING)

	override fun deserialize(decoder: Decoder): MultiblockType {
		return Multiblocks[ResourceLocation(decoder.decodeString())]!!
	}

	override fun serialize(encoder: Encoder, value: MultiblockType) {
		encoder.encodeString(value.id.toString())
	}
}