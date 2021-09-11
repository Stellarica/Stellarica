package io.github.petercrawley.minecraftstarshipplugin

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.BlockData
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPistonRetractEvent

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
}