package net.stellarica.server.networking

import net.stellarica.common.networking.Channel
import net.stellarica.common.networking.networkVersion
import net.stellarica.server.util.Tasks
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

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
				if (content.first() == networkVersion) moddedPlayers.add(event.player)
				this.unregister()
				false
			}.register()
		}
	}
}