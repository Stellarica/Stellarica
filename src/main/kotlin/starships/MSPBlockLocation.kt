package io.github.petercrawley.minecraftstarshipplugin.starships

import org.bukkit.World
import org.bukkit.block.Block

class MSPBlockLocation(var x: Int, var y: Int, var z: Int, var world: World) {
	constructor(block: Block) : this(block.x, block.y, block.z, block.world)

	fun relative(x: Int, y: Int, z: Int): MSPBlockLocation {
		return MSPBlockLocation(this.x + x, this.y + y, this.z + z, this.world)
	}

	fun bukkit(): Block {
		return world.getBlockAt(x, y, z)
	}

	override fun equals(other: Any?): Boolean {
		if (other !is MSPBlockLocation) return false

		if (x != other.x) return false
		if (y != other.y) return false
		if (z != other.z) return false
		if (world != other.world) return false

		return true
	}

	override fun hashCode(): Int {
		return 31 * (31 * (31 * (x) + y) + z) + world.hashCode()
	}
}