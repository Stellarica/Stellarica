package net.stellarica.server.craft

import net.minecraft.core.Vec3i
import net.minecraft.world.level.block.Rotation

abstract class BasicCraft : Craft {
    override fun canRotate(rotation: Rotation): Boolean {

    }
    override fun rotate(rotation: Rotation) {

    }
    override fun canMove(offset: Vec3i): Boolean {

    }
    override fun move(offset: Vec3i) {

    }
}