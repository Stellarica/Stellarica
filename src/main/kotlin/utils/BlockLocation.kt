package io.github.petercrawley.minecraftstarshipplugin.utils

import org.bukkit.block.Block

data class BlockLocation(var x: Int, var y: Int, var z: Int) {
	constructor(block: Block) : this(block.x, block.y, block.z)

	fun relative(x: Int, y: Int, z: Int): BlockLocation {
		return BlockLocation(this.x + x, this.y + y, this.z + z)
	}
}