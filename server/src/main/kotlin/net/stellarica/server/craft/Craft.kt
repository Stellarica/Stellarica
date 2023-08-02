package net.stellarica.server.craft

import net.minecraft.server.level.ServerLevel

interface Craft : BlockContainer {
	val blockCount: Int
	val world: ServerLevel
	fun checkTransformation(transformation: CraftTransformation): Boolean
	fun transform(transformation: CraftTransformation): Boolean
}