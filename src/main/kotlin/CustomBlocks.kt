package io.github.petercrawley.minecraftstarshipplugin

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.BlockData
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPhysicsEvent
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPistonRetractEvent
import org.bukkit.event.block.BlockPlaceEvent

class CustomBlocks: Listener {
	private fun mushroomBlockMovedByPiston(blocks: List<Block>, direction: BlockFace) {
		val blocksToChange: MutableMap<Block, BlockData> = mutableMapOf()

		blocks.forEach {
			if (it.type == Material.MUSHROOM_STEM) blocksToChange[it.getRelative(direction)] = it.blockData
		}

		if (blocksToChange.isNotEmpty()) {
			Bukkit.getScheduler().runTask(MinecraftStarshipPlugin.getPlugin(), Runnable {
				blocksToChange.forEach { it.key.setBlockData(it.value, false) }
			})
		}
	}

	@EventHandler
	fun mushroomBlockPushedByPiston(event: BlockPistonExtendEvent) {
		mushroomBlockMovedByPiston(event.blocks, event.direction)
	}

	@EventHandler
	fun mushroomBlockPulledByPiston(event: BlockPistonRetractEvent) {
		mushroomBlockMovedByPiston(event.blocks, event.direction)
	}

	@EventHandler
	fun mushroomBlockPlaced(event: BlockPlaceEvent) {
		val block = event.blockPlaced

		if (block.type != Material.MUSHROOM_STEM) return

		block.setBlockData(Bukkit.getServer().createBlockData(Material.MUSHROOM_STEM), false)
	}

	@EventHandler
	fun mushroomBlockPhysicsEvent(event: BlockPhysicsEvent) {
		if (event.changedType == Material.MUSHROOM_STEM) {
			event.isCancelled = true

			val blocksToUpdate = mutableSetOf<Location>()
			val checkedBlocks = mutableSetOf<Location>()

			blocksToUpdate.add(event.block.location)

			while (blocksToUpdate.isNotEmpty()) {
				val block = blocksToUpdate.first()
				blocksToUpdate.remove(block)

				if (checkedBlocks.contains(block)) continue

				checkedBlocks.add(block)

				if (block.block.type != Material.MUSHROOM_STEM) continue

				block.block.state.update(true, false)

				blocksToUpdate.add(block.clone().add( 1.0, 0.0, 0.0))
				blocksToUpdate.add(block.clone().add(-1.0, 0.0, 0.0))
				blocksToUpdate.add(block.clone().add( 0.0, 1.0, 0.0))
				blocksToUpdate.add(block.clone().add( 0.0,-1.0, 0.0))
				blocksToUpdate.add(block.clone().add( 0.0, 0.0, 1.0))
				blocksToUpdate.add(block.clone().add( 0.0, 0.0,-1.0))
			}
		}
	}
}