package io.github.hydrazinemc.hydrazine.crafts.pilotable.starships

import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.pilotedCrafts
import org.bukkit.scheduler.BukkitRunnable


/**
 * Bukkit runnable for moving starships
 * @see [Starship.move]
 */
object StarshipMover : BukkitRunnable() {
	/**
	 * Should not be called manually, as this is part of a Bukkit runnable.
	 */
	override fun run() = pilotedCrafts.forEach { if (it is Starship) it.move() }
}
