package net.stellarica.server.networking

import net.stellarica.common.networking.Channel
import net.stellarica.server.StellaricaServer.Companion.klogger
import net.stellarica.server.StellaricaServer.Companion.plugin
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener

class BukkitNetworkHandler : PluginMessageListener {
	init {
		Channel.values().forEach {
			plugin.server.messenger.registerIncomingPluginChannel(plugin, it.bukkit, this)
			plugin.server.messenger.registerOutgoingPluginChannel(plugin, it.bukkit)
		}
	}

	private val listeners = mutableSetOf<Pair<Channel, (ByteArray, Player) -> Unit>>()

	fun broadcastPacket(channel: Channel, packet: ByteArray) {
		plugin.moddedPlayers.forEach {
			sendPacket(channel, it, packet)
		}
	}

	fun sendPacket(channel: Channel, player: Player, packet: ByteArray) {
		player.sendPluginMessage(plugin, channel.bukkit, packet)
	}

	fun registerListener(channel: Channel, listener: (ByteArray, Player) -> Unit) {
		listeners.add(Pair(channel, listener))
	}

	override fun onPluginMessageReceived(channelString: String, player: Player, message: ByteArray?) {
		val channel = Channel.values().firstOrNull { it.bukkit == channelString } ?: klogger.warn {
			"Received packet on unknown channel $channelString"
		}.also { return }

		listeners.filter { it.first == channel }.forEach { it.second(message ?: byteArrayOf(), player) }
	}
}