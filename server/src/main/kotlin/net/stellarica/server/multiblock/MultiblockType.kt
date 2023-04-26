package net.stellarica.server.multiblock

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.stellarica.common.util.OriginRelative
import net.stellarica.server.material.type.block.BlockType
import net.stellarica.server.multiblock.data.EmptyMultiblockData
import net.stellarica.server.multiblock.data.MultiblockData
import net.stellarica.server.multiblock.matching.BlockMatcher
import net.stellarica.server.util.extension.toLocation
import org.bukkit.World
import java.util.UUID
import kotlin.reflect.full.primaryConstructor

abstract class MultiblockType {
	abstract val displayName: String
	abstract val id: ResourceLocation
	abstract val blocks: Map<OriginRelative, BlockMatcher>
	open val dataType: MultiblockData = EmptyMultiblockData()

	fun detect(origin: BlockPos, world: World): MultiblockInstance? {
		for (facing in setOf(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST)) {
			if (validate(facing, origin, world)) {
				return MultiblockInstance(
					UUID.randomUUID(),
					origin,
					world,
					facing,
					this,
					@Suppress("DEPRECATION")
					dataType::class.primaryConstructor!!.call() // this is in no way horribly scuffed :iea:
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
			// todo: OriginRelative.getBlockPos exists, but isn't this performant
			// should probably make that use something similar to this
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

	open fun tick(instance: MultiblockInstance) {}
	open fun init(instance: MultiblockInstance) {}
}
