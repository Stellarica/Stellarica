package net.stellarica.server.event

interface Listener<D,  E: Event<D>> {

	val priority: Priority

	fun onEvent(event: E, eventData: D)
}
