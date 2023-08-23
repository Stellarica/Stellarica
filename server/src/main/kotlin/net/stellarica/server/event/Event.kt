package net.stellarica.server.event

open class Event<D> {
	val listeners = mutableListOf<Listener<D, Event<D>>>()
	fun call(event: D) {
		listeners.sortedBy { it.priority }.forEach {
			if ((this is CancellableEvent) && cancelled) {
				cancelled = false
				return
			}
			it.onEvent(this, event)
		}
	}

	fun listen(listener: Listener<D, Event<D>>) {
		listeners.add(listener)
	}

	val isCancellable: Boolean
		get() = this is CancellableEvent
}

class CancellableEvent<EventData>: Event<EventData>() {
	var cancelled: Boolean = false
}

interface Listener<D, in E: Event<D>> {

	val priority: Priority

	fun onEvent(event: E, eventData: D)
}

enum class Priority {
	LOWEST,
	LOW,
	NORMAL,
	HIGH,
	HIGHEST
}


val testEvent = object: Event<String>() {}

fun listen() {
	testEvent.listen(object: Listener<String, Event<String>> {
		override val priority: Priority
			get() = Priority.NORMAL

		override fun onEvent(event: Event<String>, eventData: String) {
			println(eventData)
		}
	})
}