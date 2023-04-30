package net.stellarica.server.networking

import net.stellarica.common.networking.Channel
import net.stellarica.common.networking.CustomItemData
import net.stellarica.common.networking.networkVersion
import net.stellarica.server.StellaricaServer.Companion.klogger
import net.stellarica.server.material.custom.item.CustomItems
import net.stellarica.server.util.Tasks
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object ModdedPlayerHandler : Listener {
	val networkHandler = BukkitNetworkHandler()
	val moddedPlayers = mutableSetOf<Player>()

	@EventHandler
	fun onPlayerJoin(event: PlayerJoinEvent) {
		Tasks.syncDelay(20) {
			println("${event.player.name} joined, sending packet")
			networkHandler.sendPacket(Channel.LOGIN, event.player, byteArrayOf(networkVersion, 0.toByte()))

			ServerboundPacketListener(channel = Channel.LOGIN, timeout = 2000, player = event.player) { _, content ->
				println("Received login haha funny content $content from ${player!!.name}!")
				if (content.first() == networkVersion) {
					moddedPlayers.add(event.player)
					handleModdedPlayerJoin(player)
				}
				this.unregister()
				false
			}.register()
		}
	}

	@EventHandler
	fun onPlayerLeave(event: PlayerQuitEvent) {
		moddedPlayers.remove(event.player)
	}

	private fun handleModdedPlayerJoin(player: Player) {
		klogger.info { "Handling modded player sync for ${player.name}"}
		val items = CustomItems.all().map { CustomItemData(it.id, it.base.getId(), it.modelData) }
		networkHandler.sendSerializableObject(Channel.ITEM_SYNC, player, items)
	}
}