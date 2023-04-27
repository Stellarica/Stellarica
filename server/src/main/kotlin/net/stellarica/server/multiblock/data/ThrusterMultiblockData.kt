package net.stellarica.server.multiblock.data

import kotlinx.serialization.Serializable

@Serializable
class ThrusterMultiblockData : MultiblockData, FuelContainer {
	override var content: Int = 0
	override var capacity = 0
	var warmupPercentage = 0
}