package io.github.petercrawley.minecraftstarshipplugin.starships

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.getPlugin
import org.bukkit.Bukkit
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player

object StarshipManager {
    val blockMoves = mutableMapOf<Block, BlockData>()

    init {
        Bukkit.getScheduler().runTaskTimer(getPlugin(), Runnable {
            if (blockMoves.isEmpty()) return@Runnable

            blockMoves.forEach {
                it.key.setBlockData(it.value, false)
            }
        }, 1, 1)
    }

    fun getStarshipAt(block: Block, requester: Player): Starship {
        return Starship(block, requester)
    }
}