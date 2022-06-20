package io.github.hydrazinemc.hydrazine.crafts.pilotable

import io.github.hydrazinemc.hydrazine.Hydrazine
import org.bukkit.scheduler.BukkitRunnable

/**
 * Bukkit runnable for running pilot actions
 * @see [Pilotable.controlQueue]
 */
object ControlQueueRunnable : BukkitRunnable() {
	/**
	 * Should not be called manually, as this is part of a Bukkit runnable.
	 */
	override fun run() = Hydrazine.pilotedCrafts.forEach {
		while (!it.isMoving) (it.controlQueue.removeFirstOrNull() ?: return@forEach)()
	}
}
