package io.github.hydrazinemc.hydrazine.multiblocks

import io.github.hydrazinemc.hydrazine.customblocks.CustomBlocks
import org.bukkit.block.Block
import org.bukkit.block.data.MultipleFacing

/**
 * Get the custom block or material id of the block
 */
fun getId(block: Block): String = (
		CustomBlocks[block.blockData as? MultipleFacing] ?: run {
			return block.type.toString().lowercase()
		}).id.lowercase()
