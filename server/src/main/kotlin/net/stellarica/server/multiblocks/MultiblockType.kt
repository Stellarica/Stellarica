package net.stellarica.server.multiblocks

import net.minecraft.block.Block
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import net.stellarica.server.utils.coordinates.OriginRelative

data class MultiblockType(
	val id: Identifier,
	val blocks: Map<OriginRelative, Block>
) {
	fun detect(origin: BlockPos, world: World): MultiblockInstance? {
		setOf(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST).forEach { facing ->
			if (validate(facing, origin, world)) {
				return MultiblockInstance(
					origin,
					world,
					facing,
					this.id
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
			val relativeLocation = origin.add(rotatedLocation.x, rotatedLocation.y, rotatedLocation.z)
			if (world.getBlockState(relativeLocation).block != it.value) {
				return false
			} // A blocks we were expecting is missing, so break the function.
		}
		return true // Valid multiblocks of this type there
	}
}
