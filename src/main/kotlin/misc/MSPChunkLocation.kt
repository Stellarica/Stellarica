package io.github.petercrawley.minecraftstarshipplugin.misc

class MSPChunkLocation(val x: Int, val z: Int) {
	constructor(block: MSPBlockLocation): this(block.x shr 4, block.z shr 4)

	override fun equals(other: Any?): Boolean {
		if (other !is MSPChunkLocation) return false

		if (x != other.x) return false
		if (z != other.z) return false

		return true
	}

	override fun hashCode(): Int {
		return 31 * (x) + z
	}
}