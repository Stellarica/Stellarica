package net.stellarica.client

import net.fabricmc.api.ClientModInitializer
import net.stellarica.client.network.FabricNetworkHandler
import net.stellarica.common.CommonTest

@Suppress("Unused")
object StellaricaClient : ClientModInitializer {

	lateinit var networkHandler: FabricNetworkHandler

	override fun onInitializeClient() {
		println("Hello from client!")
		CommonTest().doStuff()

		networkHandler = FabricNetworkHandler()
	}
}