package io.github.hydrazinemc.hydrazine.client

import io.github.hydrazinemc.hydrazine.client.network.FabricNetworkHandler
import io.github.hydrazinemc.hydrazine.client.network.Handshake
import io.github.hydrazinemc.hydrazine.common.CommonTest
import net.fabricmc.api.ClientModInitializer

@Suppress("Unused")
object HydrazineClient : ClientModInitializer {

	lateinit var networkHandler: FabricNetworkHandler

	override fun onInitializeClient() {
		println("Hello from client!")
		CommonTest().doStuff()

		networkHandler = FabricNetworkHandler()

		val h = Handshake()
	}
}