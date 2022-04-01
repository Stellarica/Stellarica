package io.github.hydrazinemc.hydrazine.utils

import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.activeStarships
import io.github.hydrazinemc.hydrazine.starships.Starship
import org.bukkit.entity.Player

val Player.starship: Starship?
	get() {
		activeStarships.forEach{ship ->
			if (ship.passengers.contains(this)) return ship
		}
		return null
	}
val Player.isPilotingShip: Boolean
	get() = this.starship?.pilot == this