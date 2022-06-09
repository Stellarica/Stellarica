package io.github.hydrazinemc.hydrazine.utils.nms

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.LevelChunk
import org.bukkit.Location
import org.bukkit.block.data.BlockData
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld
import org.bukkit.craftbukkit.v1_19_R1.block.data.CraftBlockData

// A modified, kotlin-ified version of the block placement from
// https://github.com/APDevTeam/Movecraft/blob/main/modules/v1_18_R2/src/main/java/net/countercraft/movecraft/compat/v1_18_R2/IWorldHandler.java
// Under GPL-3 as noted in the readme

// Lol I'm too dumb for NMS, and bukkit is too slow (80k block ship = tps drop)

/**
 * Set the block at [position] in [world] to [data] using NMS
 */
private fun setBlockFast(world: Level, position: BlockPos, data: BlockState) {
	val chunk: LevelChunk = world.getChunkAt(position)
	val chunkSection = (position.y shr 4) - chunk.minSection
	var section = chunk.sections[chunkSection]
	if (section == null) {
		// Put a GLASS block to initialize the section. It will be replaced next with the real block.
		chunk.setBlockState(position, Blocks.GLASS.defaultBlockState(), false)
		section = chunk.sections[chunkSection]
	}
	if (section!!.getBlockState(position.x and 15, position.y and 15, position.z and 15) == data) {
		//Block is already of correct type and data, don't overwrite
		return
	}
	section.setBlockState(position.x and 15, position.y and 15, position.z and 15, data)
	world.sendBlockUpdated(position, data, data, 3)
	world.lightEngine.checkBlock(position) // boolean corresponds to if chunk section empty
	chunk.isUnsaved = true
}

/**
 * Set [data] to [location] using NMS
 */
fun setBlockFast(location: Location, data: BlockData) {
	val blockData = if (data is CraftBlockData) {
		data.state
	} else {
		data as BlockState
	}
	val world: Level = (location.world as CraftWorld).handle
	val blockPos = BlockPos(location.x, location.y, location.z)
	setBlockFast(world, blockPos, blockData)
}
