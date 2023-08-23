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

	fun listen(l: Event<D>.(D)->Unit, priority: Priority = Priority.NORMAL) {
		listen(object: Listener<D, Event<D>> {
			override val priority: Priority
				get() = priority

			override fun onEvent(event: Event<D>, eventData: D) {
				event.l(eventData)
			}
		})
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
	HIGHEST,
	HIGH,
	NORMAL,
	LOW,
	LOWEST
}


val testEvent = object: Event<String>() {}

fun listen() {
	testEvent.listen({

	})
}