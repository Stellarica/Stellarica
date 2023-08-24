package net.stellarica.common.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.core.Direction

object DirectionSerializer : KSerializer<Direction> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Dir", PrimitiveKind.BYTE)

	override fun deserialize(decoder: Decoder): Direction {
		return Direction.entries[decoder.decodeByte().toInt()]
	}

	override fun serialize(encoder: Encoder, value: Direction) {
		encoder.encodeByte(value.ordinal.toByte())
	}
}