package net.stellarica.server.event

open class CancellableEvent<EventData> : Event<EventData>() {
	var cancelled: Boolean = false

	fun callCancellable(event: EventData): Boolean {
		callCancellable(event)
		return !cancelled
	}

	@Deprecated("This event is cancellable, use callCancellable instead", ReplaceWith("callCancellable(event)"))
	override fun call(event: EventData) = super.call(event)
}
