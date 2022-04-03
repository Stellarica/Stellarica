package io.github.hydrazinemc.hydrazine.starships

import io.github.hydrazinemc.hydrazine.utils.BlockLocation
import io.github.hydrazinemc.hydrazine.utils.Vector3
import io.github.hydrazinemc.hydrazine.utils.nms.setBlockFast
import org.bukkit.block.data.BlockData
import org.bukkit.scheduler.BukkitRunnable
import java.util.concurrent.ConcurrentHashMap

object StarshipBlockSetter : BukkitRunnable() {
	val blockSetQueueQueue =
		ConcurrentHashMap<MutableMap<BlockLocation, BlockData>, Pair<Starship, (Vector3) -> Vector3>>() // Value is a blockSetQueue, Key is the amount of blocks.

	override fun run() {
		val targetTime = System.currentTimeMillis() + 40
		while (System.currentTimeMillis() < targetTime) {
			if (blockSetQueueQueue.isEmpty()) break

			val blockSetQueue = blockSetQueueQueue.keys.first()
			val starship = blockSetQueueQueue.values.first().first
			starship.movePassengers(blockSetQueueQueue.values.first().second)

			blockSetQueueQueue.remove(blockSetQueue)
			blockSetQueue!!.forEach {
				setBlockFast(it.key.asLocation, it.value)
			}
			starship.isMoving = false
		}
	}
}