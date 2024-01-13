package net.stellarica.server.craft

import net.minecraft.server.level.ServerLevel

interface Craft : BlockContainer {
	val blockCount: Int
	val world: ServerLevel
	fun transform(transformation: CraftTransformation): Boolean
}