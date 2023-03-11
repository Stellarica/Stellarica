package net.stellarica.server.material.custom.block

import net.minecraft.resources.ResourceLocation
import net.stellarica.server.StellaricaServer.Companion.klogger
import net.stellarica.server.material.type.block.BlockType
import net.stellarica.server.material.type.item.ItemType
import net.stellarica.server.utils.Tasks
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.BlockData
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPhysicsEvent
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPistonRetractEvent
import org.bukkit.event.block.BlockPlaceEvent

object CustomBlockHandler: Listener {
	// For our purposes BlockPistonExtendEvent and BlockPistonRetractEvent can be handled the same way.
	// TODO: There are some occasions where the block still changes, this needs to be resolved.
	private fun noteBlockMovedByPiston(blocks: List<Block>, direction: BlockFace) {
		val blocksToChange = mutableMapOf<Block, BlockData>()

		// Add each mushroom block to the list.
		blocks.forEach {
			if (it.type == Material.NOTE_BLOCK) blocksToChange[it.getRelative(direction)] = it.blockData
		}

		if (blocksToChange.isNotEmpty()) {
			// Create a task to correct the blocks after the piston is done doing its thing.
			Tasks.sync {
				blocksToChange.forEach { it.key.setBlockData(it.value, false) }
			}
		}
	}


	@EventHandler
	fun noteBlockPushedByPiston(event: BlockPistonExtendEvent) {
		noteBlockMovedByPiston(event.blocks, event.direction)
	}

	@EventHandler
	fun noteBlockPulledByPiston(event: BlockPistonRetractEvent) {
		noteBlockMovedByPiston(event.blocks, event.direction)
	}


	@EventHandler(priority = EventPriority.MONITOR)
	fun onCustomBlockPlace(event: BlockPlaceEvent) {
		if (event.blockPlaced.type != Material.NOTE_BLOCK) return
		event.blockPlaced.blockData = ItemType.of(event.itemInHand).getBlock()!!.getBukkitBlockData().also { klogger.warn {it}}
	}


	@EventHandler(priority = EventPriority.MONITOR)
	fun onCustomBlockBreak(event: BlockBreakEvent) {
		if (!event.isDropItems) return
		event.isDropItems = false // no vanilla drops
		// todo: drop
	}

	@EventHandler
	fun noteBlockPhysicsEvent(event: BlockPhysicsEvent) {
		if (event.changedType != Material.NOTE_BLOCK) return

		event.isCancelled = true

		val blocksToUpdate = mutableSetOf<Block>()
		val checkedBlocks = mutableSetOf<Block>()

		blocksToUpdate.add(event.block)

		// It is important to find every block that changed as these changes will be processed client side,
		// so we need to update ALL of them to ensure they are all correct client side.
		while (blocksToUpdate.isNotEmpty()) {
			val block = blocksToUpdate.first()
			blocksToUpdate.remove(block)

			if (checkedBlocks.contains(block)) continue

			checkedBlocks.add(block)

			if (block.type != Material.NOTE_BLOCK) continue

			Bukkit.getOnlinePlayers().forEach {
				if (it.world != event.block.world) return@forEach

				val minX = it.chunk.x - it.viewDistance
				val maxX = it.chunk.x + it.viewDistance
				val minZ = it.chunk.z - it.viewDistance
				val maxZ = it.chunk.z + it.viewDistance

				if (event.block.chunk.x !in minX..maxX) return@forEach
				if (event.block.chunk.z !in minZ..maxZ) return@forEach

				it.sendBlockChange(event.block.location, event.block.blockData)
			}

			// ew.
			blocksToUpdate.add(block.getRelative(1, 0, 0))
			blocksToUpdate.add(block.getRelative(-1, 0, 0))
			blocksToUpdate.add(block.getRelative(0, 1, 0))
			blocksToUpdate.add(block.getRelative(0, -1, 0))
			blocksToUpdate.add(block.getRelative(0, 0, 1))
			blocksToUpdate.add(block.getRelative(0, 0, -1))
		}
	}
}
