package net.stellarica.server.multiblocks

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.stellarica.common.utils.OriginRelative
import org.bukkit.World

data class MultiblockType(
	val id: ResourceLocation,
	val blocks: Map<OriginRelative, ResourceLocation>
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
			val relativeLocation = origin.offset(rotatedLocation.x, rotatedLocation.y, rotatedLocation.z)
			if (world.getBlockState(relativeLocation).block != it.value) {
				return false
			} // A blocks we were expecting is missing, so break the function.
		}
		return true // Valid multiblocks of this type there
	}
}
