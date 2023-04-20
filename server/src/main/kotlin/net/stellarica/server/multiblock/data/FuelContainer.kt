package net.stellarica.server.multiblock.data

sealed interface FuelContainer {
	var fuel: Int
	var capacity: Int
}