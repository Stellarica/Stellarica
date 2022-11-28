package io.github.hydrazinemc.hydrazine.multiblocks.events

import io.github.hydrazinemc.hydrazine.multiblocks.MultiblockInstance
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class MultiblockUnloadEvent(val multiblock: MultiblockInstance) : Event() {
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