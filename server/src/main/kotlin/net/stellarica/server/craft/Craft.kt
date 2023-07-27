package net.stellarica.server.craft

import net.minecraft.core.Vec3i
import net.minecraft.world.level.block.Rotation

interface Craft: BlockContainer {
    fun canRotate(rotation: Rotation): Boolean
    fun rotate(rotation: Rotation)
    fun canMove(offset: Vec3i): Boolean
    fun move(offset: Vec3i)
}