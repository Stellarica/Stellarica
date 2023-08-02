package net.stellarica.server.craft

import net.stellarica.common.coordinate.RelativeBlockPosition
import net.stellarica.server.multiblock.MultiblockInstance

interface MultiblockContainer: BlockContainer {
	fun getMultiblockAt(pos: RelativeBlockPosition): MultiblockInstance?

	fun addMultiblock(multiblock: MultiblockInstance)

	fun removeMultiblock(multiblock: MultiblockInstance): Boolean
}