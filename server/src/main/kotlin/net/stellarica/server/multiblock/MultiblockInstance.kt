package net.stellarica.server.multiblock

import kotlinx.serialization.Serializable
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.stellarica.common.util.OriginRelative
import net.stellarica.common.serializer.BlockPosSerializer
import net.stellarica.common.serializer.DirectionSerializer
import net.stellarica.common.serializer.UUIDSerializer
import net.stellarica.server.multiblock.data.MultiblockData
import net.stellarica.server.multiblock.type.MultiblockTypeSerializer
import net.stellarica.server.util.extension.toLocation
import net.stellarica.server.serializer.BukkitWorldSerializer
import org.bukkit.Chunk
import org.bukkit.World
import java.util.UUID

@Serializable
class MultiblockInstance(
		@Serializable(with = UUIDSerializer::class)
		val id: UUID,
		@Serializable(with = BlockPosSerializer::class)
		val origin: BlockPos,
		@Serializable(with = BukkitWorldSerializer::class)
		val world: World,
		@Serializable(with = DirectionSerializer::class)
		val direction: Direction,
		@Serializable(with = MultiblockTypeSerializer::class)
		val type: MultiblockType,
		val data: MultiblockData
) {
	val chunk: Chunk
		get() = world.getChunkAt(origin.toLocation(world))


	fun validate() = type.validate(direction, origin, world)

	/** @return the world location of [position] */
	fun getLocation(position: OriginRelative) = position.getBlockPos(origin, direction)

	/** @return the origin relative position of [loc] */
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