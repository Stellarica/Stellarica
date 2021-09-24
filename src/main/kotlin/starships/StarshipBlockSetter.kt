package io.github.petercrawley.minecraftstarshipplugin.starships

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.getPlugin
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.scheduler.BukkitRunnable
import java.util.concurrent.ConcurrentHashMap

object StarshipBlockSetter: BukkitRunnable() {
	val blockSetQueueQueue = ConcurrentHashMap<Int, MutableMap<Block, BlockData>>() // Value is a blockSetQueue, Key is the amount of blocks.

	init {this.runTaskTimer(getPlugin(), 0, 1)} // Start running the block setter

	override fun run() {
		val targetTime = System.currentTimeMillis() + 40

		while (System.currentTimeMillis() < targetTime) {
			if (blockSetQueueQueue.isEmpty()) break

			val blockSetQueue = blockSetQueueQueue.remove(blockSetQueueQueue.keys.toTypedArray().sortedArray().first()) // Messy, should get the ship with the lowest block count.

			blockSetQueue!!.forEach {
				it.key.setBlockData(it.value, false)
			}
		}
	}
}