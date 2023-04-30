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

	private val listeners = mutableMapOf<ServerboundPacketListener, Long>()

	override fun onPluginMessageReceived(channelString: String, player: Player, message: ByteArray?) {
		message ?: return

		val channel = Channel.values().firstOrNull { it.bukkit == channelString } ?: klogger.warn {
			"Received packet on unknown channel $channelString, discarding!"
		}.also { return }

		val current = System.currentTimeMillis()
		listeners.keys.removeIf { it.timeout != null && listeners[it]!! + it.timeout <= current }

		val toCall = listeners.keys.filter {
			it.channel isNullOrEq channel &&
					it.player isNullOrEq player
		}.sortedBy { it.priority }

		for (listener in toCall) {
			if (listener.callback(listener, player, message)) break
		}
	}

	fun broadcastPacket(channel: Channel, packet: ByteArray) {
		for (player in plugin.moddedPlayers) {
			sendPacket(channel, player, packet)
		}
	}

	fun sendPacket(channel: Channel, player: Player, packet: ByteArray) {
		player.sendPluginMessage(plugin, channel.bukkit, packet)
	}

	fun register(listener: ServerboundPacketListener) {
		listeners[listener] = System.currentTimeMillis()
	}

	fun unregister(listener: ServerboundPacketListener) {
		listeners.remove(listener)
	}
	private infix fun Any?.isNullOrEq(other: Any?) = this == null || this == other
}