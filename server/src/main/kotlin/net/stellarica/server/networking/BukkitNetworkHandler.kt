package net.stellarica.server.networking

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.stellarica.common.networking.Channel
import net.stellarica.server.StellaricaServer.Companion.klogger
import net.stellarica.server.StellaricaServer.Companion.plugin
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener

class BukkitNetworkHandler : PluginMessageListener {

	private val listeners = mutableMapOf<ServerboundPacketListener, Long>()
	init {
		for (channel in Channel.values()) {
			plugin.server.messenger.registerIncomingPluginChannel(plugin, channel.bukkit, this)
			plugin.server.messenger.registerOutgoingPluginChannel(plugin, channel.bukkit)
		}
	}

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

	/** Broadcast [content] on [channel] to all connected modded players */
	fun broadcastPacket(channel: Channel, packet: ByteArray) {
		for (player in plugin.moddedPlayers) {
			sendPacket(channel, player, packet)
		}
	}

	/** Broadcast [obj] (an object serializable with kotlinx.serialization) on [channel] to all connected modded players */
	inline fun <reified T : Any> broadcastSerializableObject(channel: Channel, obj: T) {
		broadcastPacket(channel, Json.encodeToString(obj).toByteArray())
	}

	/** Send [content] on [channel] to [player] */
	fun sendPacket(channel: Channel, player: Player, packet: ByteArray) {
		player.sendPluginMessage(plugin, channel.bukkit, packet)
	}

	/** Send [obj] (an object serializable with kotlinx.serialization) on [channel] to [player] */
	inline fun <reified T : Any> sendSerializableObject(channel: Channel, player: Player, obj: T) {
		sendPacket(channel, player, Json.encodeToString(obj).toByteArray())
	}

	/**
	 * Register [listener]
	 * If the listener has a timeout, it will expire that many milliseconds after this is called
	 */
	fun register(listener: ServerboundPacketListener) {
		listeners[listener] = System.currentTimeMillis()
	}

	fun unregister(listener: ServerboundPacketListener) {
		listeners.remove(listener)
	}

	private infix fun Any?.isNullOrEq(other: Any?) = this == null || this == other
}