package net.stellarica.server.multiblocks

import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import net.stellarica.server.utils.coordinates.OriginRelative

data class MultiblockInstance(
	val origin: BlockPos,
	val world: World,
	val direction: Direction,
	val typeId: Identifier
) {

	val chunk: Chunk
		get() = world.getChunk(origin)

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