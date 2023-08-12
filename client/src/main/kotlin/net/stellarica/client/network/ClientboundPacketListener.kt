package net.stellarica.client.network

import net.stellarica.client.StellaricaClient
import net.stellarica.common.networking.Channel
import net.stellarica.common.networking.NetworkHandler
import net.stellarica.common.networking.PacketListener

open class ClientboundPacketListener(
	@Suppress("UNCHECKED_CAST")
	override val handler: NetworkHandler<PacketListener> = StellaricaClient.networkHandler as NetworkHandler<PacketListener>,
	val channel: Channel? = null,
	val timeout: Long? = null,
	val priority: Int = 0,
	val callback: (ClientboundPacketListener.(ByteArray) -> Boolean)
): PacketListener