package io.github.hydrazinemc.hydrazine.utils.locations

import net.minecraft.core.BlockPos
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import kotlin.math.roundToInt

/**
 * Container for the coordinates [x] [y] and [z] of a block in [world]
 *
 * @see Vector3
 */
data class BlockLocation(var x: Int, var y: Int, var z: Int, var world: World? = null) {
	/**
	 * The bukkit [Block] at this location
	 */
	val bukkit: Block
		get() {
			return world!!.getBlockAt(x, y, z)
		}

	constructor(block: Block) : this(block.x, block.y, block.z, block.world)

	/**
	 * Note, some precision will be lost, as BlockLocation contains integers
	 */
	constructor(loc: Location) : this(loc.x.roundToInt(), loc.y.roundToInt(), loc.z.roundToInt(), loc.world)

	/**
	 * Add two BlockLocations
	 */
	operator fun plus(loc: BlockLocation) = BlockLocation(this.x + loc.x, this.y + loc.y, this.z + loc.z, this.world)

	/**
	 * Subtract two BlockLocations
	 */
	operator fun minus(loc: BlockLocation) = BlockLocation(this.x - loc.x, this.y - loc.y, this.z - loc.z, this.world)

	/**
	 * This as a [Location]
	 */
	val asLocation: Location
		get() = Location(world, x.toDouble(), y.toDouble(), z.toDouble())

	/**
	 * This as an NMS [BlockPos]
	 */
	val asBlockPos: BlockPos
		get() = BlockPos(x, y, z)

	val formattedString: String
		get() = "($x, $y, $z)"
}
