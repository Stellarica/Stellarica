package net.stellarica.server.craft

import net.minecraft.world.level.block.Rotation
import net.stellarica.common.coordinate.BlockPosition
import net.stellarica.server.util.wrapper.ServerWorld

data class CraftTransformation(
	val offset: (BlockPosition) -> BlockPosition,
	val rotation: Rotation,
	val world: ServerWorld
)
