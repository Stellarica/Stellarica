package net.stellarica.server.util

import net.stellarica.server.StellaricaServer.Companion.plugin
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

/***
 * Wrapper around the Bukkit scheduler/runnable system
 * for easy delays and async tasks.
 *
 * Heavily modified version of Tasks.kt from the IonCore/StarLegacy codebase, under MIT as noted in readme
 */
object Tasks {
	/** Run [block] as an asynchronous bukkit task */
	fun async(block: () -> Unit): BukkitTask = Bukkit.getScheduler().runTaskAsynchronously(plugin, block)

	/**
	 * Run [block] as an asynchronous bukkit task.
	 * Delay [delay] ticks before starting.
	 */
	fun asyncDelay(delay: Long, block: BukkitRunnable.() -> Unit): BukkitTask =
		Run { this.block() }.runTaskLaterAsynchronously(plugin, delay)

	/**
	 * Run [block] as an asynchronous bukkit task, repeating every [interval] ticks.
	 * Delay [delay] ticks before starting.
	 */
	fun asyncRepeat(delay: Long, interval: Long, block: BukkitRunnable.() -> Unit): BukkitTask =
		Run { this.block() }.runTaskTimerAsynchronously(plugin, delay, interval)

	/** Run [block] as a bukkit task on the next server tick */
	inline fun sync(crossinline block: BukkitRunnable.() -> Unit): BukkitTask =
		Run { this.block() }.runTask(plugin)

	/** Run [block] as a bukkit task in [delay] ticks. */
	inline fun syncDelay(delay: Long, crossinline block: BukkitRunnable.() -> Unit): BukkitTask =
		Run { this.block() }.runTaskLater(plugin, delay)


	/** Run [block] as a bukkit task in [delay] ticks, repeating every [interval] ticks. */
	inline fun syncRepeat(delay: Long, interval: Long, crossinline block: BukkitRunnable.() -> Unit): BukkitTask =
		Run { this.block() }.runTaskTimer(plugin, delay, interval)

	// this is kinda jank, but it's the best thing I could come up with
	// to allow the blocks to have the runnable context receiver.
	// (allows blocks to call cancel(), etc.)
	// can't be inline because BukkitRunnable is a class, however BukkitTask is an interface
	// might be worth looking into in the future
	class Run(inline val block: BukkitRunnable.() -> Unit) : BukkitRunnable() {
		override fun run() {
			this.block()
		}
	}
}