package io.github.petercrawley.minecraftstarshipplugin.customMaterials

import org.bukkit.Bukkit.createBlockData
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.MultipleFacing
import org.bukkit.inventory.ItemStack

enum class MaterialType {
	CustomBlock,
	CustomItem,
	Bukkit
}

class MSPMaterial {
	private var materialType: MaterialType = MaterialType.Bukkit
	private var material: Any = Material.AIR

	constructor(material: Material) {
		this.materialType = MaterialType.Bukkit
		this.material = material
	}

	constructor(material: Byte) {
		this.materialType = MaterialType.CustomBlock
		this.material = material
	}

	constructor(material: Int) {
		this.materialType = MaterialType.CustomItem
		this.material = material
	}

	fun getBukkitMaterial(): Material {
		return when (materialType) {
			MaterialType.Bukkit -> material as Material
			MaterialType.CustomBlock -> Material.MUSHROOM_STEM
			MaterialType.CustomItem -> Material.STICK
		}
	}

	fun getBukkitBlockData(): BlockData {
		return when (materialType) {
			MaterialType.Bukkit -> createBlockData(material as Material)
			MaterialType.CustomBlock -> {
				val returnValue = createBlockData(Material.MUSHROOM_STEM) as MultipleFacing

				returnValue.setFace(BlockFace.NORTH, bitOfByte(material as Byte, 5))
				returnValue.setFace(BlockFace.EAST,  bitOfByte(material as Byte, 4))
				returnValue.setFace(BlockFace.SOUTH, bitOfByte(material as Byte, 3))
				returnValue.setFace(BlockFace.WEST,  bitOfByte(material as Byte, 2))
				returnValue.setFace(BlockFace.UP,    bitOfByte(material as Byte, 1))
				returnValue.setFace(BlockFace.DOWN,  bitOfByte(material as Byte, 0))

				returnValue
			}
			MaterialType.CustomItem -> createBlockData(Material.AIR)
		}
	}

	fun getBukkitItemStack(): ItemStack {
		return when (materialType) {
			MaterialType.Bukkit -> ItemStack(material as Material)
			MaterialType.CustomBlock -> {
				val returnValue = ItemStack(Material.STICK)

				val itemMeta = returnValue.itemMeta
				itemMeta.setCustomModelData(material as Int)
				returnValue.itemMeta = itemMeta

				returnValue
			}
			MaterialType.CustomItem -> {
				val returnValue = ItemStack(Material.STICK)

				val itemMeta = returnValue.itemMeta
				itemMeta.setCustomModelData(material as Int + 192) // Offset by 192 to avoid conflicts with custom blocks
				returnValue.itemMeta = itemMeta

				returnValue
			}
		}
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as MSPMaterial

		if (materialType != other.materialType) return false
		if (material != other.material) return false

		return true
	}

	override fun hashCode(): Int {
		return 31 * materialType.hashCode() + material.hashCode()
	}
}

private fun bitOfByte(byte: Byte, bit: Int) : Boolean {
	return ((byte.toInt() shr bit) and 1) == 1
}

//	var material: Any? = material
//		set(value) {
//			when (value) {
//				is Block, is BlockData -> {
//					val originalMaterial = if (value is Block) value.type else (value as BlockData).material
//
//					val blockData = if (value is Block) value.blockData else value
//
//					if (originalMaterial == Material.MUSHROOM_STEM) {
//						val block = blockData as MultipleFacing
//						var id = 0
//
//						if (block.hasFace(BlockFace.NORTH)) id += 32
//						if (block.hasFace(BlockFace.EAST)) id += 16
//						if (block.hasFace(BlockFace.SOUTH)) id += 8
//						if (block.hasFace(BlockFace.WEST)) id += 4
//						if (block.hasFace(BlockFace.UP)) id += 2
//						if (block.hasFace(BlockFace.DOWN)) id += 1
//
//						field = customBlocks.getOrDefault(id.toByte(), "MUSHROOM_STEM")
//
//						if (material == "MUSHROOM_STEM") field = originalMaterial
//
//					} else field = originalMaterial
//				}
//				is Material -> field = value
//				is String -> field = Material.getMaterial(value) ?: if (customBlocks.values.contains(value)) value else null
//				else -> field = null
//			}
//		}
//
//	fun bukkit(): Material? {
//		return if (material is Material) material as Material else null
//	}
//
//	override fun equals(other: Any?): Boolean {
//		if (this === other) return true
//		if (javaClass != other?.javaClass) return false
//
//		other as MSPMaterial
//
//		if (material != other.material) return false
//
//		return true
//	}
//
//	override fun hashCode(): Int {
//		return material?.hashCode() ?: 0
//	}
//}