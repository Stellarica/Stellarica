package io.github.hydrazinemc.hydrazine.server.multiblocks.events

import io.github.hydrazinemc.hydrazine.server.multiblocks.MultiblockInstance
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class MultiblockDetectEvent(val multiblock: MultiblockInstance) : Event(), Cancellable {

	private var isCancelled: Boolean = false
	override fun setCancelled(cancel: Boolean) {
		isCancelled = cancel
	}

	override fun isCancelled(): Boolean {
		return isCancelled
	}

	override fun getHandlers(): HandlerList {
		return handlerList
	}

	companion object {
		/**
		 * The handlers for this event
		 */
		@JvmStatic // Apparently this is required for Paper to recognize this
		val handlerList = HandlerList()
	}
}