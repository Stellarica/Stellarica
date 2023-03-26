package net.stellarica.server.transfer.pipes

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.stellarica.server.transfer.Node
import net.stellarica.server.transfer.NodeNetwork

class PipeNetwork(
	val origin: BlockPos,
	val world: ServerLevel,
	override val primaryNode: Node<Fuel>

) : NodeNetwork<Fuel>() {
	override fun detect() {
		TODO()
	}
}