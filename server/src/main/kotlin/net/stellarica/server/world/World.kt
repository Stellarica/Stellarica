package net.stellarica.server.world

import net.minecraft.server.level.ServerLevel
import net.stellarica.common.coord.BlockPosition
import net.stellarica.server.material.type.block.BlockType
import net.stellarica.server.util.extension.toLocation
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld

class World(val bukkit: org.bukkit.World) {

    val minecraft: ServerLevel by lazy {
        (bukkit as CraftWorld).handle
    }

    fun getBlockAt(pos: BlockPosition): BlockType {
        return BlockType.of(bukkit.getBlockAt(pos.toLocation(bukkit)))
    }

    fun setBlockAt(pos: BlockPosition, type: BlockType) {
        bukkit.setBlockData(pos.x, pos.y, pos.z, type.getBukkitBlockData())
    }
}