package io.github.petercrawley.minecraftstarshipplugin.starships

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.getPlugin
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap

object StarshipManager {
    val blockSetQueue = ConcurrentHashMap<Block, BlockData>()

    val playerTeleportQueue = ConcurrentHashMap<Player, Location>()

    init {
        Bukkit.getScheduler().runTaskTimer(getPlugin(), Runnable {
            val start = System.currentTimeMillis()

            val targetTime = start + 50

            if (blockSetQueue.isNotEmpty()) {
                blockSetQueue.forEach {
                    if (System.currentTimeMillis() > targetTime) return@forEach

                    it.key.setBlockData(it.value, false)
                    blockSetQueue.remove(it.key)
                }
            }

            if (playerTeleportQueue.isNotEmpty()) {
                playerTeleportQueue.forEach {
                    if (System.currentTimeMillis() > targetTime) return@forEach

                    it.key.player?.teleport(it.value)
                    playerTeleportQueue.remove(it.key)
                }
            }
        }, 1, 1)
    }

    fun getStarshipAt(block: Block, requester: Player): Starship {
        return Starship(block, requester)
    }
}
