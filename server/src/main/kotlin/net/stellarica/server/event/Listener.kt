package net.stellarica.server.event

interface Listener<D, E : Event<D>> {
	val event: E

	val priority: Priority

	fun onEvent(event: E, data: D)

	fun unregister() {
		@Suppress("UNCHECKED_CAST") // puffer // todo: dejankify
		event.unregister(this as Listener<D, Event<D>>)
	}
}
