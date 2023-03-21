package net.stellarica.server.multiblocks

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.stellarica.common.utils.OriginRelative
import net.stellarica.server.material.type.block.BlockType
import net.stellarica.server.multiblocks.matching.BlockMatcher
import net.stellarica.server.utils.extensions.toLocation
import org.bukkit.World

data class MultiblockType(
	val displayName: String,
	val id: ResourceLocation,
	val blocks: Map<OriginRelative, BlockMatcher>
) {
	fun detect(origin: BlockPos, world: World): MultiblockInstance? {
		setOf(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST).forEach { facing ->
			if (validate(facing, origin, world)) {
				return MultiblockInstance(
					origin,
					world,
					facing,
					this
				)
			}
		}
		return null
	}

	/**
	 * Whether the collection of blocks at [origin] in [world] matches this multiblocks type
	 */
	fun validate(facing: Direction, origin: BlockPos, world: World): Boolean {
		fun rotationFunction(it: OriginRelative) = when (facing) {
			Direction.NORTH -> it
			Direction.EAST -> OriginRelative(-it.z, it.y, it.x)
			Direction.SOUTH -> OriginRelative(-it.x, it.y, -it.z)
			Direction.WEST -> OriginRelative(it.z, it.y, -it.x)

			else -> throw IllegalArgumentException("Invalid multiblocks facing direction: $facing")
		}

		blocks.forEach {
			val rotatedLocation = rotationFunction(it.key)
			val relativeLocation = origin.offset(rotatedLocation.x, rotatedLocation.y, rotatedLocation.z)
			if (!it.value.matches(BlockType.of(world.getBlockState(relativeLocation.toLocation(world))))) {
				return false
			} // A blocks we were expecting is missing, so break the function.
		}
		return true // Valid multiblocks of this type there
	}
}
