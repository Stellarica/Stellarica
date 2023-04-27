package net.stellarica.server.multiblock.data

sealed interface FuelContainer {
	var content: Int
	var capacity: Int
}