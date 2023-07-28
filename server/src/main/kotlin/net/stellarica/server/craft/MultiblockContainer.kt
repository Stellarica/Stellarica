package net.stellarica.server.craft

import net.stellarica.common.coordinate.RelativeBlockPosition
import net.stellarica.server.multiblock.MultiblockInstance

interface MultiblockContainer: BlockContainer {
	fun getMultiblockAt(pos: RelativeBlockPosition)

	fun addMultiblock(multiblock: MultiblockInstance)

	fun removeMultiblock(multiblock: MultiblockInstance)
}