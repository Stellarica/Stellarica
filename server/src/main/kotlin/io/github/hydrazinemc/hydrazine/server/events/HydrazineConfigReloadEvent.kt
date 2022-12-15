package io.github.hydrazinemc.hydrazine.server.events

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Called when the configuration file is reloaded
 */
class HydrazineConfigReloadEvent : Event() {
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
