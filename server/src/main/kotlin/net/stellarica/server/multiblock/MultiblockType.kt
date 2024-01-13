package net.stellarica.server.multiblock

import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.stellarica.common.coordinate.BlockPosition
import net.stellarica.common.coordinate.RelativeBlockPosition
import net.stellarica.server.material.block.type.BlockType
import net.stellarica.server.multiblock.matching.BlockMatcher
import net.stellarica.server.util.extension.toLocation
import org.bukkit.World
import java.util.UUID

abstract class MultiblockType {
	abstract val displayName: String
	abstract val id: ResourceLocation
	abstract val blocks: Map<RelativeBlockPosition, BlockMatcher>

	fun detectMultiblock(origin: BlockPosition, world: World): MultiblockInstance? {
		for (facing in setOf(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST)) {
			if (validatePattern(facing, origin, world)) {
				return MultiblockInstance(
					UUID.randomUUID(),
					origin,
					world,
					facing,
					this,
				)
			}
		}
		return null
	}

	/**
	 * Whether the collection of blocks at [origin] in [world] matches this multiblocks type
	 */
	fun validatePattern(facing: Direction, origin: BlockPosition, world: World): Boolean {
		blocks.forEach { (relPos, matcher) ->
			val globalPos = relPos.getGlobalPosition(origin, facing)
			if (!matcher.matches(BlockType.of(world.getBlockState(globalPos.toLocation(world))))) {
				return false
			} // A blocks we were expecting is missing, so break the function.
		}
		return true // Valid multiblocks of this type there
	}
}
