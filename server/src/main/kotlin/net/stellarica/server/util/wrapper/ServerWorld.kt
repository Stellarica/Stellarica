package net.stellarica.server.util.wrapper

import net.minecraft.server.level.ServerLevel
import net.stellarica.common.coordinate.BlockPosition
import net.stellarica.server.material.block.type.BlockType
import org.bukkit.Chunk
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

	fun getBlockTypeAt(pos: BlockPosition): BlockType {
		// this feels slightly dirty, is there a better way?
		// also nullability? what if it's not loaded there, pretty sure getBlockAt force sync loads the chunk
		return BlockType.of(bukkit.getBlockAt(pos.x, pos.y, pos.z).blockData)
	}

	fun getChunkAt(pos: BlockPosition): Chunk {
		return bukkit.getChunkAt(pos.x shr 4, pos.z shr 4)
	}

	override fun hashCode(): Int = vanilla.hashCode()
	override fun equals(other: Any?): Boolean {
		// I really don't like having this
		return if (other is ServerWorld) {
			vanilla == other.vanilla
		} else {
			// hmmmmm
			vanilla == other
		}
	}
}
