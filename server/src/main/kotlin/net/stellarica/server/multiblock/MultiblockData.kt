package net.stellarica.server.multiblock

import kotlinx.serialization.Serializable

@Serializable
sealed interface MultiblockData

@Serializable
open class EmptyMultiblockData : MultiblockData {
	var power = 0
}

@Serializable
open class PowerableMultiblockData : MultiblockData {
	var power = 0
}