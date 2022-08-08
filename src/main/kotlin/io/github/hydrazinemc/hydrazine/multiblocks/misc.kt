package io.github.hydrazinemc.hydrazine.multiblocks

import io.github.hydrazinemc.hydrazine.Hydrazine
import io.github.hydrazinemc.hydrazine.customblocks.CustomBlocks
import org.bukkit.Chunk
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.block.data.MultipleFacing

/**
 * Get the custom block or material id of the block
 */
fun getId(block: Block): String = (
		CustomBlocks[block.blockData as? MultipleFacing] ?: run {
			return block.type.toString().lowercase()
		}).id.lowercase()


/**
 * The multiblocks in this chunk
 * Backed by the Chunk's PDC
 */
var Chunk.multiblocks: MutableSet<MultiblockInstance>
	get() = this.persistentDataContainer.get(
		NamespacedKey(Hydrazine.plugin, "multiblocks"),
		MultiblockPDC
	) ?: mutableSetOf()
	set(value) = this.persistentDataContainer.set(
		NamespacedKey(Hydrazine.plugin, "multiblocks"),
		MultiblockPDC,
		value
	)
