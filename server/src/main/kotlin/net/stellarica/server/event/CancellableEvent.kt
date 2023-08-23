package net.stellarica.server.event

open class CancellableEvent<EventData>: Event<EventData>() {
	var cancelled: Boolean = false

	fun callCancellable(event: EventData): Boolean {
		call(event)
		return !cancelled
	}
}