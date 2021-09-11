package io.github.petercrawley.minecraftstarshipplugin.customblocks

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.MultipleFacing

object CustomBlocks {
	private val customBlocks = mutableMapOf(
		Pair(0b000000, "INTERFACE")
	)

	fun bukkitToCustom(bukkit: Block): String {
		if (bukkit.type == Material.MUSHROOM_STEM) {
			val block = bukkit.blockData as MultipleFacing
			var id = 0

			if (block.hasFace(BlockFace.DOWN))  id += 32
			if (block.hasFace(BlockFace.EAST))  id += 16
			if (block.hasFace(BlockFace.NORTH)) id +=  8
			if (block.hasFace(BlockFace.SOUTH)) id +=  4
			if (block.hasFace(BlockFace.UP))    id +=  2
			if (block.hasFace(BlockFace.WEST))  id +=  1

			return customBlocks.getOrDefault(id, "MUSHROOM_STEM")
		}

		return bukkit.type.name
	}

	fun customToBukkit(custom: String): BlockData {
		customBlocks.forEach {
			if (it.value == custom) {
				val blockData = Bukkit.getServer().createBlockData(Material.MUSHROOM_STEM) as MultipleFacing
				var id = it.key

				if (id >= 32) id -= 32; blockData.setFace(BlockFace.DOWN,  true)
				if (id >= 16) id -= 16; blockData.setFace(BlockFace.EAST,  true)
				if (id >=  8) id -=  8; blockData.setFace(BlockFace.NORTH, true)
				if (id >=  4) id -=  4; blockData.setFace(BlockFace.SOUTH, true)
				if (id >=  2) id -=  2; blockData.setFace(BlockFace.UP,    true)
				if (id >=  1) id -=  1; blockData.setFace(BlockFace.WEST,  true)

				return blockData
			}
		}

		return Bukkit.getServer().createBlockData(Material.getMaterial(custom)!!)
	}
}