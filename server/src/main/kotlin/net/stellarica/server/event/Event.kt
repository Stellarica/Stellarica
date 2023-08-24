package net.stellarica.server.event

open class Event<D> {
	private val listeners = mutableListOf<Listener<D, Event<D>>>()

	fun call(event: D) {
		if (this is CancellableEvent) cancelled = false

		listeners.sortedBy { it.priority }.forEach {
			if ((this is CancellableEvent) && cancelled) {
				return
			}
			it.onEvent(this, event)
		}
	}

	fun listen(listener: Listener<D, Event<D>>) {
		listeners.add(listener)
	}

	fun listen(l: Listener<D, Event<D>>.(D)->Unit, priority: Priority = Priority.NORMAL) {
		listen(object: Listener<D, Event<D>> {
			override val event = this@Event
			override val priority: Priority
				get() = priority

			override fun onEvent(event: Event<D>, eventData: D) {
				this.l(eventData)
			}
		})
	}

	fun unregister(listener: Listener<D, Event<D>>) {
		assert(listeners.remove(listener))
	}

	val isCancellable: Boolean
		get() = this is CancellableEvent
}
