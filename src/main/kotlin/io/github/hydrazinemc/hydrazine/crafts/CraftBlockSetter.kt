package io.github.hydrazinemc.hydrazine.crafts

import io.github.hydrazinemc.hydrazine.utils.locations.BlockLocation
import io.github.hydrazinemc.hydrazine.utils.nms.removeBlockEntity
import io.github.hydrazinemc.hydrazine.utils.nms.setBlockEntity
import io.github.hydrazinemc.hydrazine.utils.nms.setBlockFast
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import org.bukkit.block.data.BlockData
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld
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
		// go through as many craft moves as we can within 40ms
		while (System.currentTimeMillis() < targetTime) {
			if (blockSetQueueQueue.isEmpty()) break

			val moveData = blockSetQueueQueue.values.first()
			val blockSetQueue = blockSetQueueQueue.keys.first()

			moveData.craft.movePassengers(moveData.modifier, moveData.rotation)

			blockSetQueueQueue.remove(blockSetQueue)

			// get nms tile entities, and remove them
			val entities = mutableMapOf<BlockEntity, Pair<Level, BlockPos>>() // pair is target
			moveData.entities.forEach {
				val world = (it.value.world as CraftWorld).handle
				entities[removeBlockEntity(world, it.key.asBlockPos) ?: return@forEach] = Pair(world, it.value.asBlockPos)
			}

			// move blocks
			blockSetQueue!!.forEach {
				setBlockFast(it.key.asLocation, it.value)
			}

			// set entities
			entities.forEach {(entity, pos) ->
				setBlockEntity(pos.first, pos.second, entity)
			}

			// let the craft know we're done here
			moveData.craft.isMoving = false
		}
	}
}
