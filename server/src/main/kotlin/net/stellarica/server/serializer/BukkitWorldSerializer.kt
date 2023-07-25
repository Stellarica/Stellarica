package net.stellarica.server.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.stellarica.common.serializer.ResourceLocationSerializer
import net.stellarica.server.StellaricaServer.Companion.plugin
import net.stellarica.server.util.extension.toNamespacedKey
import net.stellarica.server.util.extension.toResourceLocation
import org.bukkit.World

object BukkitWorldSerializer : KSerializer<World> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("World", PrimitiveKind.STRING)

	override fun deserialize(decoder: Decoder): World {
		return plugin.server.getWorld(decoder.decodeSerializableValue(ResourceLocationSerializer).toNamespacedKey())!!
	}

	override fun serialize(encoder: Encoder, value: World) {
		encoder.encodeSerializableValue(ResourceLocationSerializer, value.key.toResourceLocation())
	}
}