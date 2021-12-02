package io.github.petercrawley.minecraftstarshipplugin.events

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class MSPConfigReloadEvent : Event() {
	override fun getHandlers(): HandlerList {
		return handlerList
	}

	companion object {
		val handlerList = HandlerList()
	}
}
