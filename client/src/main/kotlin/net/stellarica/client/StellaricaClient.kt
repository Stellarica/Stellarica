package net.stellarica.client

import net.stellarica.client.network.FabricNetworkHandler
import net.stellarica.client.network.Handshake
import net.stellarica.common.CommonTest
import net.fabricmc.api.ClientModInitializer

@Suppress("Unused")
object StellaricaClient : ClientModInitializer {

	lateinit var networkHandler: FabricNetworkHandler

	override fun onInitializeClient() {
		println("Hello from client!")
		CommonTest().doStuff()

		networkHandler = FabricNetworkHandler()

		val h = Handshake()
	}
}