package net.stellarica.server.multiblock

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.serializer
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.stellarica.server.StellaricaServer.Companion.plugin
import net.stellarica.server.multiblock.data.MultiblockData
import java.util.UUID

class MultiblockSerializer : KSerializer<MultiblockInstance> {
	override val descriptor: SerialDescriptor = buildClassSerialDescriptor("MultiblockInstance") {
		element<String>("type")
		element<List<Int>>("origin")
		element<String>("world")
		element<Byte>("direction")
		element<UUID>("id")
		element<MultiblockData>("data")
	}
	@OptIn(InternalSerializationApi::class)
	override fun deserialize(decoder: Decoder): MultiblockInstance {
		val type = Multiblocks.byId(ResourceLocation.tryParse(decoder.decodeString())!!)
		@Suppress("UNCHECKED_CAST") val origin = (decoder.decodeSerializableValue(serializer(listOf<Int>()::class.java))
				as List<Int>).let { BlockPos(it[0], it[1], it[2]) }
		val world = decoder.decodeString().let { plugin.server.getWorld(it)!! }
		val direction = Direction.values()[decoder.decodeByte().toInt()]
		val id = decoder.decodeSerializableValue(UUID::class.serializer())
		val data = decoder.decodeSerializableValue(MultiblockData::class.serializer())
		return MultiblockInstance(id, origin, world, direction, type!!, data)
	}

	@OptIn(InternalSerializationApi::class)
	override fun serialize(encoder: Encoder, value: MultiblockInstance) {
		encoder.encodeStructure(descriptor) {
			encodeStringElement(descriptor, 0, value.type.id.toString())
			encodeSerializableElement(descriptor, 1, serializer(listOf<Int>()::class.java), listOf(value.origin.x, value.origin.y, value.origin.z))
			encodeStringElement(descriptor, 2, value.world.name)
			encodeByteElement(descriptor, 3, value.direction.ordinal.toByte())
			encodeSerializableElement(descriptor, 4, UUID::class.serializer(), value.id)
			encodeSerializableElement(descriptor, 5, MultiblockData::class.serializer(), value.data)
		}
	}
}