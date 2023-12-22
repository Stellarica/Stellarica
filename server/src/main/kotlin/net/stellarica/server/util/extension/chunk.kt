package net.stellarica.server.util.extension

import net.minecraft.core.BlockPos
import net.minecraft.world.level.chunk.LevelChunk
import org.bukkit.craftbukkit.v1_20_R3.CraftChunk

val org.bukkit.Chunk.vanilla: LevelChunk
	// cursed :solution: because CraftChunk.handle seems to have mysteriously vanished...
	get() = (this as CraftChunk).craftWorld.handle.getChunkAt(BlockPos(this.x, 0, this.z))

val LevelChunk.bukkit: org.bukkit.Chunk
	// another cursed :solution: because LevelChunk.bukkitChunk seems to have vanished
	get() = this.level.world.getChunkAt(this.locX, this.locZ)