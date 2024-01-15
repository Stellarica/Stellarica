package net.stellarica.server.multiblock

import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.stellarica.common.coordinate.BlockPosition
import net.stellarica.common.coordinate.RelativeBlockPosition
import net.stellarica.server.multiblock.matching.BlockMatcher
import net.stellarica.server.util.wrapper.ServerWorld

abstract class MultiblockType {
	abstract val displayName: String
	abstract val id: ResourceLocation
	abstract val blocks: Map<RelativeBlockPosition, BlockMatcher>

	/**
	 * Whether the collection of blocks at [origin] in [world] matches this multiblocks type
	 */
	fun validatePattern(facing: Direction, origin: BlockPosition, world: ServerWorld): Boolean {
		blocks.forEach { (relPos, matcher) ->
			val globalPos = relPos.getGlobalPosition(origin, facing)
			if (!matcher.matches(world.getBlockTypeAt(globalPos))) {
				return false
			} // A blocks we were expecting is missing, so break the function.
		}
		return true // Valid multiblocks of this type there
	}
}
