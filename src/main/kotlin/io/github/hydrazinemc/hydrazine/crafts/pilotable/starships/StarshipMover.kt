package io.github.hydrazinemc.hydrazine.crafts.pilotable.starships

import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.klogger
import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.pilotedCrafts
import io.github.hydrazinemc.hydrazine.utils.Vector3
import org.bukkit.scheduler.BukkitRunnable
import kotlin.system.measureTimeMillis


/**
 * Main bukkit runnable for moving starships
 */
object StarshipMover : BukkitRunnable() {

	private var ticksSinceMove = 0
	var movesPerSecond = 2f
		private set(value) {
			field = value.coerceIn(0.5f, 5f)
		}

	/**
	 * Should not be called manually, as this is part of a Bukkit runnable.
	 */
	override fun run() {
		ticksSinceMove++
		if (ticksSinceMove >= 20 / movesPerSecond) {
			val timeSpentMoving = measureTimeMillis {
				pilotedCrafts.forEach {ship ->
					if (ship !is Starship) return@forEach
					ship.velocity += ship.acceleration / movesPerSecond
					if (ship.velocity == Vector3.zero) return@forEach
					if (ship.isMoving) {
						klogger.warn {"A ship needs to move again, but hasn't finished moving!"}
						return@forEach
					}
					ship.queueMovement((ship.velocity / movesPerSecond).asBlockLocation)
				}
			}
			if (timeSpentMoving < 20) movesPerSecond++
			if (timeSpentMoving > 30) movesPerSecond--
			ticksSinceMove = 0
		}
	}
}
