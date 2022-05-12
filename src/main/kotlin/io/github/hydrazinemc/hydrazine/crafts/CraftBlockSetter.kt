package io.github.hydrazinemc.hydrazine.crafts

import io.github.hydrazinemc.hydrazine.utils.BlockLocation
import io.github.hydrazinemc.hydrazine.utils.nms.setBlockFast
import org.bukkit.block.data.BlockData
import org.bukkit.scheduler.BukkitRunnable
import java.util.concurrent.ConcurrentHashMap

/**
 * Main bukkit runnable for setting craft's blocks
 */
object CraftBlockSetter : BukkitRunnable() {
	/**
	 * The queue of crafts to move
	 * Key is the blocks to set, value is the extra data for this move operation
	 */
	val blockSetQueueQueue =
		ConcurrentHashMap<
				MutableMap<BlockLocation, BlockData>,
				CraftMoveData
				>()

	/**
	 * Should not be called manually, as this is part of a Bukkit runnable.
	 *
	 * Moves as many blockSetQueues from [blockSetQueueQueue] as it can in 40 ms.
	 * Can spend over 40ms running when moving large queues=
	 */
	override fun run() {
		val targetTime = System.currentTimeMillis() + 40
		while (System.currentTimeMillis() < targetTime) {
			if (blockSetQueueQueue.isEmpty()) break

			val moveData = blockSetQueueQueue.values.first()
			val blockSetQueue = blockSetQueueQueue.keys.first()

			moveData.craft.movePassengers(moveData.modifier, moveData.rotation)

			blockSetQueueQueue.remove(blockSetQueue)
			blockSetQueue!!.forEach {
				setBlockFast(it.key.asLocation, it.value)
			}
			moveData.craft.isMoving = false
		}
	}
}