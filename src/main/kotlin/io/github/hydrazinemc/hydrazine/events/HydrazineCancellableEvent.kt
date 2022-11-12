package io.github.hydrazinemc.hydrazine.events

import org.bukkit.event.Cancellable

open class HydrazineCancellableEvent: HydrazineEvent(), Cancellable {
	private var isCancelled: Boolean = false
	override fun setCancelled(cancel: Boolean) {
		isCancelled = cancel
	}

	override fun isCancelled(): Boolean {
		return isCancelled
	}
}