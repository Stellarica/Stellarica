package io.github.petercrawley.minecraftstarshipplugin.customblocks

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.MultipleFacing

class MSPMaterial {
	private val customBlocks = mutableMapOf(
		Pair(0b000000, "INTERFACE")
	)

	private var material: Any?

	init {
		material = null
	}

	constructor(value: Any) {
		set(value)
	}

	fun getBukkit(): Material? {
		return if (material is Material) material as Material else null
	}

	fun set(value: Any?) {
		when (value) {
			is Block -> {
				if (value.type == Material.MUSHROOM_STEM) {
					val block = value.blockData as MultipleFacing
					var id = 0

					if (block.hasFace(BlockFace.DOWN))  id += 32
					if (block.hasFace(BlockFace.EAST))  id += 16
					if (block.hasFace(BlockFace.NORTH)) id +=  8
					if (block.hasFace(BlockFace.SOUTH)) id +=  4
					if (block.hasFace(BlockFace.UP))    id +=  2
					if (block.hasFace(BlockFace.WEST))  id +=  1

					material = customBlocks.getOrDefault(id, "MUSHROOM_STEM")

					if (material == "MUSHROOM_STEM") material = value.type

				} else material = value.type
			}
			is Material -> material = value
			is String -> material = Material.getMaterial(value) ?: if (customBlocks.values.contains(value)) value else null
			else -> material = null
		}
	}

	fun get(): Any? {
		return material
	}

	override fun hashCode(): Int {
		return material?.hashCode() ?: 0
	}

	override fun toString(): String {
		return material.toString()
	}

	override fun equals(other: Any?): Boolean {
		return if (other is MSPMaterial) material == other.material else material == other
	}
}