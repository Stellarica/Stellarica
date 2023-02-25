package net.stellarica.server.multiblocks

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.stellarica.common.utils.OriginRelative
import net.stellarica.server.utils.extensions.toLocation
import org.bukkit.Chunk
import org.bukkit.World

data class MultiblockInstance(
	val origin: BlockPos,
	val world: World,
	val direction: Direction,
	val typeId: ResourceLocation
) {

	val chunk: Chunk
		get() = world.getChunkAt(origin.toLocation(world))

	val type: MultiblockType // this seems inefficient
		get() = MultiblockHandler.types.first { it.id == typeId }

	fun validate() = type.validate(direction, origin, world)

	/**
	 * @return the world location of [position]
	 */
	fun getLocation(position: OriginRelative) = position.getBlockPos(origin, direction)

	/**
	 * @return the origin relative position of [loc]
	 */
	fun getOriginRelative(loc: BlockPos) = OriginRelative.get(loc, this.origin, this.direction)


	/**
	 * @return whether this contains a blocks at [loc].
	 * Does not take into account world
	 */
	fun contains(loc: BlockPos): Boolean {
		type.blocks.keys.forEach {
			if (it == getOriginRelative(loc)) return true
		}
		return false
	}

}