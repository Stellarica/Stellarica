package net.stellarica.server.craft

interface Subcraft : Craft, BlockContainer {
	val parent: CraftContainer
}
