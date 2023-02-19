package net.stellarica.server.multiblocks.events

import net.stellarica.server.multiblocks.MultiblockInstance
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class MultiblockLoadEvent(val multiblock: MultiblockInstance) : Event() {
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