package net.stellarica.common.networking

interface NetworkHandler<L : PacketListener> {
	val listeners: MutableMap<L, Long>

	/**
	 * Register [listener]
	 * If the listener has a timeout, it will expire that many milliseconds after this is called
	 */
	fun register(listener: L) {
		listeners[listener] = System.currentTimeMillis()
	}

	fun unregister(listener: L) {
		listeners.remove(listener)
	}
}