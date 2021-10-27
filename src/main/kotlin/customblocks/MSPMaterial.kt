package io.github.petercrawley.minecraftstarshipplugin.customblocks

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.customBlocks
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.MultipleFacing

class MSPMaterial(material: Any?) {
	var material: Any? = material
		set(value) {
			when (value) {
				is Block, is BlockData -> {
					val originalMaterial = if (value is Block) value.type else (value as BlockData).material

					val blockData = if (value is Block) value.blockData else value

					if (originalMaterial == Material.MUSHROOM_STEM) {
						val block = blockData as MultipleFacing
						var id = 0

						if (block.hasFace(BlockFace.NORTH)) id += 32
						if (block.hasFace(BlockFace.EAST)) id += 16
						if (block.hasFace(BlockFace.SOUTH)) id += 8
						if (block.hasFace(BlockFace.WEST)) id += 4
						if (block.hasFace(BlockFace.UP)) id += 2
						if (block.hasFace(BlockFace.DOWN)) id += 1

						field = customBlocks.getOrDefault(id.toByte(), "MUSHROOM_STEM")

						if (material == "MUSHROOM_STEM") field = originalMaterial

					} else field = originalMaterial
				}
				is Material -> field = value
				is String -> field = Material.getMaterial(value) ?: if (customBlocks.values.contains(value)) value else null
				else -> field = null
			}
		}

	fun bukkit(): Material? {
		return if (material is Material) material as Material else null
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as MSPMaterial

		if (material != other.material) return false

		return true
	}

	override fun hashCode(): Int {
		return material?.hashCode() ?: 0
	}
}