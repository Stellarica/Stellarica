package io.github.hydrazinemc.hydrazine.utils

import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.plugin
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import java.time.ZonedDateTime

/***
 * Wrapper around the Bukkit scheduler/runnable system
 * for easy delays and async tasks.
 *
 * Modified version of Tasks.kt from the IonCore/StarLegacy codebase, under MIT as noted in readme
 */
object Tasks {
	/**
	 * Run [block] as an asynchronous bukkit task
	 *
	 * @see BukkitScheduler.runTaskAsynchronously
	 */
	fun async(block: () -> Unit): BukkitTask = Bukkit.getScheduler().runTaskAsynchronously(plugin, block)

	/**
	 * Run [block] as an asynchronous bukkit task.
	 * Delay [delay] ticks before starting.
	 *
	 * @see BukkitScheduler.runTaskLaterAsynchronously
	 */
	fun asyncDelay(delay: Long, block: () -> Unit): BukkitTask =
		Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, block, delay)

	/**
	 * Run [block] as an asynchronous bukkit task, repeating every [interval] ticks.
	 * Delay [delay] ticks before starting.
	 *
	 * @see BukkitScheduler.runTaskTimerAsynchronously
	 */
	fun asyncRepeat(delay: Long, interval: Long, block: () -> Unit): BukkitTask =
		Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, block, delay, interval)

	/**
	 * Run [block] as a bukkit task on the next server tick
	 * @see BukkitScheduler.runTask
	 * @see BukkitScheduler.callSyncMethod
	 */
	inline fun sync(crossinline block: () -> Unit): BukkitTask {
		return Bukkit.getScheduler().runTask(plugin, Runnable { block() })
	}

	/**
	 * Run [block] as a bukkit task in [delay] ticks.
	 * @see BukkitScheduler.runTaskLater
	 */
	inline fun syncDelay(delay: Long, crossinline block: () -> Unit): BukkitTask {
		return Bukkit.getScheduler().runTaskLater(plugin, Runnable { block() }, delay)
	}

	/**
	 * Run [block] as a bukkit task in [delay] ticks, repeating every [interval] ticks.
	 * @see BukkitScheduler.runTaskTimer
	 */
	inline fun syncRepeat(delay: Long, interval: Long, crossinline block: () -> Unit): BukkitTask =
		Bukkit.getScheduler().runTaskTimer(plugin, Runnable { block() }, delay, interval)

	/**
	 * Run [block] as an async task once per day
	 * @param hour Hour of day, 0-23
	 *
	 * Leftover from SL code
	 */
	fun asyncAtHour(hour: Int, block: () -> Unit) {
		require(hour in 0..23)

		val now: ZonedDateTime = ZonedDateTime.now()

		var time: ZonedDateTime = now.withHour(hour)

		if (time.isBefore(now) || time.isEqual(now)) {
			time = now.plusDays(1).withHour(hour)
		}

		// Why not toEpochMillis?
		// this whole thing seems dumb, but I don't want to touch it if it works.
		val delay = (time.toEpochSecond() * 1000L) - System.currentTimeMillis()
		check(delay > 0)

		println("SCHEDULED TASK FOR $time, SUPPLIED HOUR OF DAY $hour, ACTUAL HOUR OF DAY ${time.hour}")

		asyncDelay(delay / 50L, block)
	}
}
