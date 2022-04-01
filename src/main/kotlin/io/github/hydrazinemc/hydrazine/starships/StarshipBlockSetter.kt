package io.github.hydrazinemc.hydrazine.starships

import io.github.hydrazinemc.hydrazine.utils.BlockLocation
import org.bukkit.Bukkit
import org.bukkit.block.data.BlockData
import org.bukkit.scheduler.BukkitRunnable
import java.util.concurrent.ConcurrentHashMap

object StarshipBlockSetter : BukkitRunnable() {
	val blockSetQueueQueue =
		ConcurrentHashMap<MutableMap<BlockLocation, BlockData>, Starship>() // Value is a blockSetQueue, Key is the amount of blocks.

	override fun run() {
		val targetTime = System.currentTimeMillis() + 40
		while (System.currentTimeMillis() < targetTime) {
			if (blockSetQueueQueue.isEmpty()) break

			val blockSetQueue = blockSetQueueQueue.keys.first()
			blockSetQueueQueue.remove(blockSetQueue)

			blockSetQueue!!.forEach {
				it.key.bukkit.setBlockData(it.value, false)
			}
		}
	}
}