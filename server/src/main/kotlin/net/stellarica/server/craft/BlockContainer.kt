package net.stellarica.server.craft

import net.minecraft.core.Direction
import net.stellarica.common.coordinate.BlockPosition
import net.stellarica.common.coordinate.RelativeBlockPosition

interface BlockContainer {
    val origin: BlockPosition
    val orientation: Direction

    fun getGlobalPos(pos: RelativeBlockPosition): BlockPosition {
        return pos.getGlobalPosition(origin, orientation)
    }

    fun getRelativePos(pos: BlockPosition): RelativeBlockPosition {
        return pos.getAsRelative(origin, orientation)
    }

    fun contains(block: BlockPosition): Boolean
}