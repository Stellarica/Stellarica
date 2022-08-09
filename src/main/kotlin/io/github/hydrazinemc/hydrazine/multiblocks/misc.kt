package io.github.hydrazinemc.hydrazine.multiblocks

import io.github.hydrazinemc.hydrazine.Hydrazine
import io.github.hydrazinemc.hydrazine.customblocks.CustomBlocks
import org.bukkit.Chunk
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.block.data.MultipleFacing


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
