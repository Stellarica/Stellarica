package net.stellarica.server.craft

import net.minecraft.core.Vec3i
import net.minecraft.world.level.block.Rotation
import net.stellarica.common.coordinate.BlockPosition
import org.bukkit.World

interface Craft : BlockContainer {
	val blockCount: Int
	val world: World
	fun canRotate(rotation: Rotation): Boolean
	fun rotate(rotation: Rotation)
	fun canMove(offset: Vec3i): Boolean
	fun move(offset: Vec3i)
	fun canTeleport(pos: BlockPosition, world: World): Boolean
	fun teleport(pos: BlockPosition, world: World)
}