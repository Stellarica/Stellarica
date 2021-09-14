package io.github.petercrawley.minecraftstarshipplugin.starships

import org.bukkit.block.Block
import org.bukkit.entity.Player

object StarshipManager {
    fun getStarshipAt(block: Block, requester: Player): Starship {
        return Starship(block, requester)
    }
}