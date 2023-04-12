package net.stellarica.server.transfer.pipes

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.Blocks
import net.stellarica.common.utils.OriginRelative
import net.stellarica.server.transfer.Network
import net.stellarica.server.transfer.nodes.Node


class PipeNetwork(
	var origin: BlockPos,
	var world: ServerLevel,
	var direction: Direction = Direction.NORTH,
	override var nodes: MutableMap<OriginRelative, Node> = mutableMapOf()
): Network() {
	override val maxTransferRate = PipeHandler.maxTransferRate

	fun isCopper(pos: OriginRelative) : Boolean = world.getBlockState(pos(pos)).block == Blocks.WAXED_COPPER_BLOCK || isInput(pos)
	fun isRod(pos: OriginRelative) : Boolean = world.getBlockState(pos(pos)).block == Blocks.LIGHTNING_ROD
	fun isInput(pos: OriginRelative) : Boolean = world.getBlockState(pos(pos)).block == Blocks.WAXED_CUT_COPPER
	fun isOutput(pos: OriginRelative) : Boolean = world.getBlockState(pos(pos)).block == Blocks.WAXED_CUT_COPPER_SLAB
	fun pos(pos: OriginRelative) = pos.getBlockPos(origin, direction)

	fun contains(pos: BlockPos) = nodes[OriginRelative.getOriginRelative(pos, origin, direction)] != null


	fun isInLoadedChunks(): Boolean {
		for (pos in nodes.keys) {
			// jank alert
			if (world.getChunkIfLoaded(OriginRelative.getBlockPos(pos, origin, direction)) != null) return true
		}
		return false
	}
}

