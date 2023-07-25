package net.stellarica.common.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.IntArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.core.BlockPos

object BlockPosSerializer : KSerializer<BlockPos> {
	@OptIn(ExperimentalSerializationApi::class)
	override val descriptor: SerialDescriptor = SerialDescriptor("BlockPos", IntArraySerializer().descriptor)

	override fun deserialize(decoder: Decoder): BlockPos {
		val array = decoder.decodeSerializableValue(IntArraySerializer())
		return BlockPos(array[0], array[1], array[2])
	}

	override fun serialize(encoder: Encoder, value: BlockPos) {
		encoder.encodeSerializableValue(IntArraySerializer(), intArrayOf(value.x, value.y, value.z))
	}
}