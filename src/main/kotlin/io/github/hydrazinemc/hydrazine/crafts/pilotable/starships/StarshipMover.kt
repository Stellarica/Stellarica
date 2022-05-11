package io.github.hydrazinemc.hydrazine.crafts.pilotable.starships

import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.pilotedCrafts
import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.plugin
import io.github.hydrazinemc.hydrazine.utils.Vector3
import org.bukkit.scheduler.BukkitRunnable


/**
 * Main bukkit runnable for moving starships
 */
object StarshipMover : BukkitRunnable() {

	var tickCounter = 0
		private set

	val movesPerSecond: Float
		get() = 20 / ticksPerMove.toFloat()

	var ticksPerMove = 0
		private set
	/**
	 * Should not be called manually, as this is part of a Bukkit runnable.
	 */
	override fun run() {
		tickCounter++
		if (tickCounter >= ticksPerMove) {
			pilotedCrafts.forEach {ship ->
				if (ship !is Starship) return@forEach
				if (ship.velocty == Vector3.zero) return@forEach
				if (ship.isMoving) return@forEach

				ship.queueMovement((ship.velocty / movesPerSecond).asBlockLocation)
				ship.messagePilot("|Velocity: <bold>(${ship.velocty.x}, ${ship.velocty.y}, ${ship.velocty.z})")
				ship.messagePilot("<gray>$movesPerSecond moves per second")
			}
			tickCounter = 0
			ticksPerMove = plugin.server.averageTickTime.toInt()
		}
	}
}