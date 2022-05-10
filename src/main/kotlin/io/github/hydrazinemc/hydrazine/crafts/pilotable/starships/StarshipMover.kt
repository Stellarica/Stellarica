package io.github.hydrazinemc.hydrazine.crafts.pilotable.starships

import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.pilotedCrafts
import io.github.hydrazinemc.hydrazine.utils.Vector3
import org.bukkit.scheduler.BukkitRunnable


/**
 * Main bukkit runnable for moving starships
 */
object StarshipMover : BukkitRunnable() {

	/**
	 * Should not be called manually, as this is part of a Bukkit runnable.
	 *
	 * Moves all starships with a non-zero velocity
	 */
	override fun run() {
		pilotedCrafts.forEach {ship ->
			if (ship !is Starship) return@forEach
			if (ship.velocty == Vector3.zero) return@forEach
			if (ship.isMoving) return@forEach
			ship.queueMovement(ship.velocty.asBlockLocation)
			ship.messagePilot("""
				
				<gray>Ship Movement!
				|Velocity: ${ship.velocty}
				|Acceleration: ${ship.acceleration}
			""".trimIndent())
		}
	}
}