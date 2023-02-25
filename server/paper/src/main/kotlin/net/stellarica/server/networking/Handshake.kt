package net.stellarica.server.networking

import net.stellarica.common.networkVersion
import net.stellarica.common.networking.Channel
import net.stellarica.server.StellaricaServer.Companion.klogger
import net.stellarica.server.StellaricaServer.Companion.plugin
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class Handshake : Listener {
	init {
		plugin.networkHandler.registerListener(Channel.HANDSHAKE, ::onPlayerRespond)
	}

	@EventHandler
	fun onPlayerLeave(event: PlayerQuitEvent) {
		plugin.moddedPlayers.remove(event.player)
	}

	fun onPlayerRespond(packet: ByteArray, player: Player) {
		klogger.info { "Received handshake packet from $player" }
		if (player in plugin.moddedPlayers) {
			klogger.warn { "Player $player already connected! Either this is a bug or the player is spoofing packets!" }
			return
		}
		val clientVersion = packet.first()
		when {
			clientVersion == networkVersion -> {
				klogger.info { "Player $player connected with compatible mod version" }
				plugin.moddedPlayers.add(player)
			}

			clientVersion > networkVersion -> klogger.warn {
				"Player $player has a newer version of the mod than the server! " +
						"Please update the plugin! ($clientVersion > $networkVersion)"
			}

			else -> klogger.info {
				"Player $player has an outdated version of the mod! ($clientVersion < $networkVersion)"
			}
		}

		plugin.networkHandler.sendPacket(Channel.HANDSHAKE, player, byteArrayOf(networkVersion))
	}
}