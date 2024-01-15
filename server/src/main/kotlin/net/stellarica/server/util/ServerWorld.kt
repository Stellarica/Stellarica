package net.stellarica.server.util

import net.minecraft.server.level.ServerLevel
import org.bukkit.World
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld

class ServerWorld {
	val vanilla: ServerLevel
	val bukkit: World
		get() = vanilla.world

	constructor(world: ServerLevel) {
		vanilla = world
	}
	constructor(world: World) {
		vanilla = (world as CraftWorld).handle
	}
}