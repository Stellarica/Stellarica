package net.stellarica.server.event

open class CancellableEvent<EventData> : Event<EventData>() {
	var cancelled: Boolean = false

	fun callCancellable(data: EventData): Boolean {
		callCancellable(data)
		return !cancelled
	}

	@Deprecated("This event is cancellable, use callCancellable instead", ReplaceWith("callCancellable(event)"))
	override fun call(data: EventData) = super.call(data)
}
