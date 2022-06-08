package io.github.hydrazinemc.hydrazine.multiblocks

import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.plugin
import io.github.hydrazinemc.hydrazine.customblocks.CustomBlocks
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.MultipleFacing

data class MultiblockLayout(val name: String) {
	val blocks = mutableMapOf<MultiblockOriginRelative, String>()
}

data class Multiblock(
	val name: String,
	val x: Int,
	val y: Int,
	val z: Int,
	val r: Byte,
	var t: Int = 0
)

fun getId(block: Block): String = (
		CustomBlocks[block.blockData as? MultipleFacing] ?: run {
			return block.type.toString().lowercase()
		}).id.lowercase()
