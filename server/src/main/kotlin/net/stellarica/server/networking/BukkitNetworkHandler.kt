package net.stellarica.server.networking

import net.stellarica.common.networking.Channel
import net.stellarica.server.StellaricaServer.Companion.klogger
import net.stellarica.server.StellaricaServer.Companion.plugin
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener

class BukkitNetworkHandler : PluginMessageListener {
	init {
		for (channel in Channel.values()) {
			plugin.server.messenger.registerIncomingPluginChannel(plugin, channel.bukkit, this)
			plugin.server.messenger.registerOutgoingPluginChannel(plugin, channel.bukkit)
		}
	}

	private val listeners = mutableSetOf<Pair<Channel, (ByteArray, Player) -> Unit>>()

	fun broadcastPacket(channel: Channel, packet: ByteArray) {
		for (player in plugin.moddedPlayers) {
			sendPacket(channel, player, packet)
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