package io.github.hydrazinemc.hydrazine.utils

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import kotlin.math.roundToInt

/**
 * @see Vector3
 */
data class BlockLocation(var x: Int, var y: Int, var z: Int, var world: World?) {
	val bukkit: Block
		get() {
			return world!!.getBlockAt(x, y, z)
		}

	constructor(block: Block) : this(block.x, block.y, block.z, block.world)

	/**
	 * Note, some precision will be lost, as BlockLocation contains integers
	 */
	constructor(loc: Location) : this(loc.x.roundToInt(), loc.y.roundToInt(), loc.z.roundToInt(), loc.world)

	operator fun plus(loc: BlockLocation) = BlockLocation(this.x + loc.x, this.y + loc.y, this.z + loc.z, this.world)
	operator fun minus(loc: BlockLocation) = BlockLocation(this.x - loc.x, this.y - loc.y, this.z - loc.z, this.world)

	val asLocation: Location
		get() = Location(world, x.toDouble(), y.toDouble() ,z.toDouble())
}


data class ChunkLocation(var x: Int, var z: Int)