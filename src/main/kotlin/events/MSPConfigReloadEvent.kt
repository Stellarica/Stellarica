package io.github.petercrawley.minecraftstarshipplugin.events

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class MSPConfigReloadEvent : Event() {
	override fun getHandlers(): HandlerList {
		return handlerList
	}

	companion object {
		@JvmStatic // Apparently this is required for Paper to recognize this
		val handlerList = HandlerList()
	}
}