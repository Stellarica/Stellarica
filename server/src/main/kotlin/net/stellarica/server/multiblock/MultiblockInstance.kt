package net.stellarica.server.multiblock

import kotlinx.serialization.Serializable
import net.minecraft.core.Direction
import net.stellarica.common.coordinate.BlockPosition
import net.stellarica.common.serializer.DirectionSerializer
import net.stellarica.common.serializer.UUIDSerializer
import net.stellarica.server.craft.BlockContainer
import net.stellarica.server.multiblock.type.MultiblockTypeSerializer
import net.stellarica.server.serializer.BukkitWorldSerializer
import org.bukkit.World
import java.util.UUID

@Serializable
class MultiblockInstance(
	@Serializable(with = UUIDSerializer::class)
	val id: UUID,
	override var origin: BlockPosition,
	@Serializable(with = BukkitWorldSerializer::class)
	val world: World,
	@Serializable(with = DirectionSerializer::class)
	override var orientation: Direction,
	@Serializable(with = MultiblockTypeSerializer::class)
	val type: MultiblockType,
) : BlockContainer {

	fun validate() = type.validatePattern(orientation, origin, world)

	override fun contains(block: BlockPosition): Boolean {
		return type.blocks.keys.contains(getRelativePos(block))
	}
}