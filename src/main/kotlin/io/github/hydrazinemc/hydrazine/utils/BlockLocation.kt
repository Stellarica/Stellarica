package io.github.hydrazinemc.hydrazine.utils

import org.bukkit.World
import org.bukkit.block.Block

data class BlockLocation(var x: Int, var y: Int, var z: Int, var world: World?) {
	val bukkit: Block
		get() {
			return world!!.getBlockAt(x, y, z)
		}

	constructor(block: Block) : this(block.x, block.y, block.z, block.world)

	fun relative(x: Int, y: Int, z: Int): BlockLocation {
		return BlockLocation(this.x + x, this.y + y, this.z + z, this.world)
	}
}