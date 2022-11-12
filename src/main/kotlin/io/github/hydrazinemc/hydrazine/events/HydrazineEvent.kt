package io.github.hydrazinemc.hydrazine.events

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Utility class for creating events without all the boilerplate
 */
open class HydrazineEvent : Event() {
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
