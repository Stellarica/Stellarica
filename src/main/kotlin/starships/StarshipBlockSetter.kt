package io.github.petercrawley.minecraftstarshipplugin.starships

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.getPlugin
import io.github.petercrawley.minecraftstarshipplugin.utils.BlockLocation
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.scheduler.BukkitRunnable
import java.util.concurrent.ConcurrentHashMap

object StarshipBlockSetter: BukkitRunnable() {
	val blockSetQueueQueue = ConcurrentHashMap<MutableMap<BlockLocation, BlockData>, Boolean>() // Value is a blockSetQueue, Key is the amount of blocks.

	init {this.runTaskTimer(getPlugin(), 0, 1)} // Start running the block setter

	override fun run() {
		val targetTime = System.currentTimeMillis() + 40

		while (System.currentTimeMillis() < targetTime) {
			if (blockSetQueueQueue.isEmpty()) break

			val blockSetQueue = blockSetQueueQueue.keys.first()

			blockSetQueue!!.forEach {
				it.key.bukkit.setBlockData(it.value, false)
			}
		}
	}
}