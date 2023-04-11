package net.stellarica.server.multiblocks

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.stellarica.common.utils.OriginRelative
import net.stellarica.server.utils.extensions.toLocation
import org.bukkit.Chunk
import org.bukkit.World
import java.util.UUID

data class MultiblockInstance(
	val id: UUID,
	val origin: BlockPos,
	val world: World,
	val direction: Direction,
	val type: MultiblockType,
	val data: MultiblockData
) {

	val chunk: Chunk
		get() = world.getChunkAt(origin.toLocation(world))


	fun validate() = type.validate(direction, origin, world)

	/**
	 * @return the world location of [position]
	 */
	fun getLocation(position: OriginRelative) = position.getBlockPos(origin, direction)

	/**
	 * @return the origin relative position of [loc]
	 */
	fun getOriginRelative(loc: BlockPos) = OriginRelative.getOriginRelative(loc, this.origin, this.direction)


	/**
	 * @return whether this contains a blocks at [loc].
	 * Does not take into account world
	 */
	fun contains(loc: BlockPos): Boolean {
		for (pos in type.blocks.keys) {
			if (pos == getOriginRelative(loc)) return true
		}
		return false
	}
}