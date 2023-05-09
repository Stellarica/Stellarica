package net.stellarica.server.util.persistence

import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataContainer

class PlayerPersistentStorage(private val player: Player): PersistentDataContainerStorage() {
	override fun getPersistentDataContainer(): PersistentDataContainer {
		return player.persistentDataContainer
	}
}