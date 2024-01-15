package net.stellarica.server.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.stellarica.common.serializer.ResourceLocationSerializer
import net.stellarica.server.StellaricaServer.Companion.plugin
import net.stellarica.server.util.wrapper.ServerWorld
import net.stellarica.server.util.extension.toNamespacedKey
import net.stellarica.server.util.extension.toResourceLocation

object WorldSerializer : KSerializer<ServerWorld> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("World", PrimitiveKind.STRING)

	override fun deserialize(decoder: Decoder): ServerWorld {
		return ServerWorld(plugin.server.getWorld(decoder.decodeSerializableValue(ResourceLocationSerializer).toNamespacedKey())!!)
	}

	override fun serialize(encoder: Encoder, value: ServerWorld) {
		encoder.encodeSerializableValue(ResourceLocationSerializer, value.bukkit.key.toResourceLocation())
	}
}
