package io.github.hydrazinemc.hydrazine.crafts.pilotable.starships

import io.github.hydrazinemc.hydrazine.utils.Tasks
import io.github.hydrazinemc.hydrazine.utils.gui.setScoreboardContents
import org.bukkit.entity.Player

object StarshipHUD {
	private val ships = mutableSetOf<Starship>()

	init {
		Tasks.syncRepeat(1, 1) {
			ships.forEach { ship ->
				display(ship)
			}
		}
	}

	fun open(ship: Starship) = ships.add(ship)

	fun close(ship: Starship) {
		ships.remove(ship)
		ship.passengers.forEach {
			(it as? Player)?.setScoreboardContents("", listOf())
		}
	}

	private fun display(ship: Starship) {
		ship.passengers.forEach {
			(it as? Player)?.setScoreboardContents("Starship", listOf(
				"Block Count: ${ship.blockCount}",
				"Origin: ${ship.origin.formattedString}",
				"Shields: ${ship.shields.shieldHealth}/${ship.shields.maxShieldHealth}",
			))
		}
	}
}