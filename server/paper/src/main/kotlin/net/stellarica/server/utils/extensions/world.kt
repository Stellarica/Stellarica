package net.stellarica.server.utils.extensions

import org.bukkit.craftbukkit.v1_19_R3.CraftWorld


val org.bukkit.World.vanilla: net.minecraft.server.level.ServerLevel
	get() = (this as CraftWorld).handle

val net.minecraft.server.level.ServerLevel.bukkit: org.bukkit.World
	get() = this.world