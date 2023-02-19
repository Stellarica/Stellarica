package net.stellarica.server.crafts.pilotables

import org.bukkit.scheduler.BukkitRunnable

/**
 * Bukkit runnable for running pilot actions
 * @see [Pilotable.controlQueue]
 */
object ControlQueueRunnable : BukkitRunnable() {
	/**
	 * Should not be called manually, as this is part of a Bukkit runnable.
	 */
	override fun run() = net.stellarica.server.StellaricaServer.pilotedCrafts.forEach {
		while (!it.isMoving) (it.controlQueue.removeFirstOrNull() ?: return@forEach)()
	}
}
