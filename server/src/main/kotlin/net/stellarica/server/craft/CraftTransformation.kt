package net.stellarica.server.craft

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.Rotation
import net.stellarica.common.coordinate.BlockPosition

data class CraftTransformation(
	val offset: (BlockPosition) -> BlockPosition,
	val rotation: Rotation,
	val world: ServerLevel
)