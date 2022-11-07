package io.github.hydrazinemc.hydrazine.multiblocks

import io.github.hydrazinemc.hydrazine.Hydrazine
import org.bukkit.Chunk
import org.bukkit.NamespacedKey


/**
 * The multiblocks in this chunk
 * Backed by the Chunk's PDC
 */
var Chunk.multiblocks: Set<MultiblockInstance>
	get() = this.persistentDataContainer.get(
		NamespacedKey(Hydrazine.plugin, "multiblocks"),
		MultiblockPDC
	) ?: mutableSetOf()
	set(value) = this.persistentDataContainer.set(
		NamespacedKey(Hydrazine.plugin, "multiblocks"),
		MultiblockPDC,
		value
	)
