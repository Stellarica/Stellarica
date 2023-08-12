package net.stellarica.common.networking

interface PacketListener {
	val handler: NetworkHandler<PacketListener>
	fun unregister() {
		handler.unregister(this)
	}
	fun register() {
		handler.register(this)
	}
}