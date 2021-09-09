package io.github.petercrawley.minecraftstarshipplugin.ships

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block

class MSPLocation {
	var world: World = Bukkit.getWorlds()[0]
	var x: Int = 0
	var y: Int = 0
	var z: Int = 0

	constructor(world: World, x: Int, y: Int, z: Int) {
		this.world = world
		this.x = x
		this.y = y
		this.z = z
	}

	constructor(bukkit: Location) {
		world = bukkit.world
		x = bukkit.blockX
		y = bukkit.blockY
		z = bukkit.blockZ
	}

	fun block(): Block {
		return world.getBlockAt(x, y, z)
	}

	fun add(x: Int, y: Int, z: Int): MSPLocation {
		return MSPLocation(world, this.x + x, this.y + y, this.z + z)
	}

	override fun equals(other: Any?): Boolean {
		if (other !is MSPLocation) return false
		if (other.world != world) return false
		if (other.x != x) return false
		if (other.y != y) return false
		if (other.z != z) return false
		return true
	}

	// Don't know what this does but IntelliJ wanted it.
	// Originally IntelliJ generated it like this but I simplified it.
	//	override fun hashCode(): Int {
	//		var result = world.hashCode()
	//		result = 31 * result + x
	//		result = 31 * result + y
	//		result = 31 * result + z
	//		return result
	//	}
	override fun hashCode(): Int {
		return 31 * (31 * (31 * world.hashCode() + x) + y) + z
	}
}
