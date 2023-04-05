package net.stellarica.server.utils.extensions

import net.minecraft.world.level.chunk.LevelChunk
import org.bukkit.craftbukkit.v1_19_R3.CraftChunk

val org.bukkit.Chunk.vanilla: LevelChunk
	get() = (this as CraftChunk).handle
