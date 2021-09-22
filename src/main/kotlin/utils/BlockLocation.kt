package io.github.petercrawley.minecraftstarshipplugin.utils

import org.bukkit.block.Block

data class BlockLocation(var x: Int, var y: Int, var z: Int) {
	constructor(block: Block) : this(block.x, block.y, block.z)
}