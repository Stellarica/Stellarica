package io.github.hydrazinemc.hydrazine.crafts.pilotable.starships

import io.github.hydrazinemc.hydrazine.utils.Tasks
import io.github.hydrazinemc.hydrazine.utils.gui.ScoreboardDisplay
import org.bukkit.entity.Player

object StarshipHUD: ScoreboardDisplay() {
	private val ships = mutableSetOf<Starship>()

	init {
		Tasks.syncRepeat(1, 1) {
			ships.forEach { ship ->
				display(ship)
			}
		}
	}


	fun open(ship: Starship) {
		ships.add(ship)
	}
	fun close(ship: Starship) {
		ships.remove(ship)
		ship.passengers.forEach { passenger ->
			if (passenger is Player) {
				passenger.scoreboard = passenger.server.scoreboardManager.mainScoreboard
			}
		}
	}

	private fun display(ship: Starship) {
		ship.passengers.forEach {
			val player = it as? Player ?: return@forEach
			setScoreboard(player, "Starship", listOf(
				"Block Count: ${ship.blockCount}",
				"Origin: ${ship.origin.formattedString}",
				"Shields: ${ship.shields.shieldHealth}/${ship.shields.maxShieldHealth}",
			))
		}
	}
}