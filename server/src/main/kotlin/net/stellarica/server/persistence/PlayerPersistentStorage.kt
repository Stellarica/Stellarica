package net.stellarica.server.persistence

import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataContainer

class PlayerPersistentStorage(private val player: Player): PersistentDataContainerStorage() {
	override fun getPersistentDataContainer(): PersistentDataContainer {
		return player.persistentDataContainer
	}

	override fun isValid(): Boolean = player.isOnline
}