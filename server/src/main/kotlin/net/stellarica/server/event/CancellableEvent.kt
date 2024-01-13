package net.stellarica.server.event

open class CancellableEvent<EventData> : Event<EventData>() {
	var cancelled: Boolean = false

	fun call(data: EventData): Boolean {
		@Suppress("DEPRECATION")
		invoke(data)
		return !cancelled
	}

	@Deprecated("This event is cancellable, use call instead", ReplaceWith("call(data)"))
	override operator fun invoke(data: EventData) = super.invoke(data)
}
