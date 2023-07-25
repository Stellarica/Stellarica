package net.stellarica.server.persistence

import org.bukkit.World
import org.bukkit.persistence.PersistentDataContainer

class WorldPersistentStorage(val world: World): PersistentDataContainerStorage() {
	override fun getPersistentDataContainer(): PersistentDataContainer {
		return world.persistentDataContainer
	}

	override fun isValid() = true
}