package net.stellarica.server.craft

import net.stellarica.server.util.ServerWorld

interface Craft : BlockContainer {
	val blockCount: Int
	val world: ServerWorld
	fun transform(transformation: CraftTransformation): Boolean
}