package io.github.petercrawley.minecraftstarshipplugin.starships

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.getPlugin
import io.github.petercrawley.minecraftstarshipplugin.utils.BlockLocation
import org.bukkit.block.data.BlockData
import org.bukkit.scheduler.BukkitRunnable
import java.util.concurrent.ConcurrentHashMap

object StarshipBlockSetter: BukkitRunnable() {
	val blockSetQueueQueue = ConcurrentHashMap<MutableMap<BlockLocation, BlockData>, Starship>() // Value is a blockSetQueue, Key is the amount of blocks.

	init {this.runTaskTimer(getPlugin(), 0, 1)} // Start running the block setter

	override fun run() {
		val targetTime = System.currentTimeMillis() + 40

		while (System.currentTimeMillis() < targetTime) {
			if (blockSetQueueQueue.isEmpty()) break

			val blockSetQueue = blockSetQueueQueue.keys.first()
			val starship = blockSetQueueQueue.remove(blockSetQueue)

			blockSetQueue!!.forEach {
				it.key.bukkit.setBlockData(it.value, false)
			}

			starship!!.isWaiting = false
		}
	}
}