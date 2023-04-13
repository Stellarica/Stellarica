package net.stellarica.server.transfer.pipe

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.Blocks
import net.stellarica.common.util.OriginRelative
import net.stellarica.server.transfer.Network
import net.stellarica.server.transfer.node.Node
import net.stellarica.server.util.extension.toLocation
import net.stellarica.server.material.type.block.BlockType
import net.stellarica.server.util.extension.type
import org.bukkit.World


class PipeNetwork(
	var origin: BlockPos,
	var world: World,
	var direction: Direction = Direction.NORTH,
	override var nodes: MutableMap<OriginRelative, Node> = mutableMapOf()
): Network() {
	override val maxTransferRate = PipeHandler.maxTransferRate

	fun isCopper(pos: OriginRelative) : Boolean = typeAt(pos) == Blocks.WAXED_COPPER_BLOCK.type || isInput(pos)
	fun isRod(pos: OriginRelative) : Boolean = typeAt(pos) == Blocks.LIGHTNING_ROD.type
	fun isInput(pos: OriginRelative) : Boolean = typeAt(pos) == Blocks.WAXED_CUT_COPPER.type
	fun isOutput(pos: OriginRelative) : Boolean = typeAt(pos) == Blocks.WAXED_CUT_COPPER_SLAB.type
	fun pos(pos: OriginRelative) = pos.getBlockPos(origin, direction)

	fun contains(pos: BlockPos) = nodes[OriginRelative.getOriginRelative(pos, origin, direction)] != null
	fun typeAt(pos: OriginRelative) = BlockType.of(world.getBlockState(pos(pos).toLocation(world)))

	fun isInLoadedChunks(): Boolean {
		for (pos in nodes.keys) {
			val c = OriginRelative.getBlockPos(pos, origin, direction)
			if (world.isChunkLoaded(world.getChunkAt(c.toLocation(world)))) return true
		}
		return false
	}
}

