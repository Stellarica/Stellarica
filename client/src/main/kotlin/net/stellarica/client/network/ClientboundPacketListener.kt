package net.stellarica.client.network

import net.stellarica.client.StellaricaClient
import net.stellarica.common.networking.Channel
import net.stellarica.common.networking.PacketListener

open class ClientboundPacketListener(
	override val handler: FabricNetworkHandler = StellaricaClient.networkHandler,
	val channel: Channel? = null,
	val timeout: Long? = null,
	val priority: Int = 0,
	val callback: (ClientboundPacketListener.(ByteArray) -> Boolean)
): PacketListener