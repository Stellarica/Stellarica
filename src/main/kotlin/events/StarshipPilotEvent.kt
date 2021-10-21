package io.github.petercrawley.minecraftstarshipplugin.events

import io.github.petercrawley.minecraftstarshipplugin.starships.Starship
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class StarshipPilotEvent(val starship: Starship, val player: Player) : Event() {
	// TODO: actually call this

	override fun getHandlers(): HandlerList {
		return handlerList
	}

	companion object {
		val handlerList = HandlerList()
	}
}