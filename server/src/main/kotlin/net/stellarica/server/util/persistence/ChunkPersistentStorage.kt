package net.stellarica.server.util.persistence;

import org.bukkit.Chunk
import org.bukkit.persistence.PersistentDataContainer

class ChunkPersistentStorage(private val chunk: Chunk) : PersistentDataContainerStorage() {
	override fun getPersistentDataContainer(): PersistentDataContainer {
		return chunk.persistentDataContainer
	}
}