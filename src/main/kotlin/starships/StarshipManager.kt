package io.github.petercrawley.minecraftstarshipplugin.starships

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.getPlugin
import org.bukkit.Bukkit
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap

object StarshipManager {
    val blockMoves = ConcurrentHashMap<Block, BlockData>()

    init {
        Bukkit.getScheduler().runTaskTimer(getPlugin(), Runnable {
            if (blockMoves.isEmpty()) return@Runnable

            val initialBlocks = blockMoves.size

            val start = System.currentTimeMillis()

            val targetTime = start + 45

            blockMoves.forEach {
                if (System.currentTimeMillis() > targetTime) return@forEach

                it.key.setBlockData(it.value, false)
                blockMoves.remove(it.key)
            }

            val timeTook = System.currentTimeMillis() - start

            getPlugin().logger.info("$timeTook $initialBlocks")
        }, 1, 1)
    }

    fun getStarshipAt(block: Block, requester: Player): Starship {
        return Starship(block, requester)
    }
}
