package io.github.hydrazinemc.hydrazine.crafts

import io.github.hydrazinemc.hydrazine.multiblocks.multiblocks
import io.github.hydrazinemc.hydrazine.utils.Vector3
import io.github.hydrazinemc.hydrazine.utils.locations.BlockLocation
import io.github.hydrazinemc.hydrazine.utils.nms.removeBlockEntity
import io.github.hydrazinemc.hydrazine.utils.nms.setBlockEntity
import io.github.hydrazinemc.hydrazine.utils.nms.setBlockFast
import io.github.hydrazinemc.hydrazine.utils.rotation.RotationAmount
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import org.bukkit.block.data.BlockData
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld
import org.bukkit.material.Directional
import org.bukkit.scheduler.BukkitRunnable
import rotateBlockFace
import java.util.concurrent.ConcurrentHashMap
import kotlin.system.measureTimeMillis

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
	 * Moves blockSetQueues from [blockSetQueueQueue].
	 */
	override fun run() {
		while (blockSetQueueQueue.isNotEmpty()) {

			val moveData = blockSetQueueQueue.values.first()
			val blockSetQueue = blockSetQueueQueue.keys.first()

			val timeSpent = measureTimeMillis {
				moveData.craft.movePassengers(moveData.modifier, moveData.rotation)

				blockSetQueueQueue.remove(blockSetQueue)

				// get nms tile entities, and remove them
				val entities = mutableMapOf<BlockEntity, Pair<Level, BlockPos>>() // pair is target
				moveData.entities.forEach {
					val world = (moveData.world as CraftWorld).handle
					entities[removeBlockEntity(world, it.key.asBlockPos) ?: return@forEach] =
						Pair(world, it.value.asBlockPos)
				}

				// move blocks
				blockSetQueue!!.forEach {
					setBlockFast(it.key.asLocation, it.value)
				}

				// set entities
				entities.forEach { (entity, pos) ->
					setBlockEntity(pos.first, pos.second, entity)
				}

				// move multiblocks
				moveData.craft.multiblocks.forEach {multiblock ->
					// Figure out where to go
					val oldLoc = multiblock.origin.clone()
					val newLoc = moveData.modifier(Vector3(oldLoc)).asLocation.apply { this@apply.world = moveData.world}

					// Update the old chunk
					oldLoc.chunk.multiblocks = oldLoc.chunk.multiblocks.filter { it != multiblock }.toMutableSet()

					// Update the multiblock itself
					multiblock.origin = newLoc
					multiblock.facing = rotateBlockFace(multiblock.facing, moveData.rotation)

					// Update the new chunk
					val nmb = newLoc.chunk.multiblocks
					nmb.add(multiblock)
					newLoc.chunk.multiblocks = nmb
				}

				// let the craft know we're done here
				moveData.craft.isMoving = false
			}
			moveData.craft.timeSpentMoving = timeSpent
		}
	}
}
