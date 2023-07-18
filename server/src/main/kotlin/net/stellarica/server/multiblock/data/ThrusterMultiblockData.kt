package net.stellarica.server.multiblock.data

import kotlinx.serialization.Serializable

@Serializable
class ThrusterMultiblockData : MultiblockData {
	var warmupPercentage = 0
}