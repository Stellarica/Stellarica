package net.stellarica.common.networking

interface NetworkHandler<L : PacketListener> {
	val listeners: MutableMap<L, Long>

	operator fun plusAssign(listener: L) = register(listener)
	operator fun minusAssign(listener: L) = unregister(listener)

	fun register(listener: L) {
		listeners[listener] = System.currentTimeMillis()
	}

	fun unregister(listener: L) {
		listeners.remove(listener)
	}
}
