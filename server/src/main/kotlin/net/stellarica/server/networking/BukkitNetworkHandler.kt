package net.stellarica.server.networking

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.encodeToByteArray
import net.stellarica.common.networking.Channel
import net.stellarica.common.networking.NetworkHandler
import net.stellarica.server.StellaricaServer.Companion.klogger
import net.stellarica.server.StellaricaServer.Companion.plugin
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener

@OptIn(ExperimentalSerializationApi::class)
class BukkitNetworkHandler : PluginMessageListener, NetworkHandler<ServerboundPacketListener> {

	override val listeners = mutableMapOf<ServerboundPacketListener, Long>()

	init {
		for (channel in Channel.entries) {
			plugin.server.messenger.registerIncomingPluginChannel(plugin, channel.bukkit, this)
			plugin.server.messenger.registerOutgoingPluginChannel(plugin, channel.bukkit)
		}
	}

	override fun onPluginMessageReceived(channelString: String, player: Player, message: ByteArray?) {
		message ?: return

		val channel = Channel.entries.firstOrNull { it.bukkit == channelString } ?: run {
			klogger.warn { "Received packet on unknown channel $channelString, discarding!" }
			return
		}


		listeners.keys
			.also { keys ->
				val current = System.currentTimeMillis()
				keys.removeIf { it.timeout != null && listeners[it]!! + it.timeout <= current }
			}
			.filter { (it.channel ?: channel) == channel && (it.player ?: player) == player }
			.sortedBy { it.priority }
			.forEach { if (it.callback(it, player, message)) return }
	}

	/** Broadcast [content] on [channel] to all connected modded players */
	fun broadcastPacket(channel: Channel, packet: ByteArray) {
		for (player in ModdedPlayerHandler.moddedPlayers) {
			sendPacket(channel, player, packet)
		}
	}

	/** Broadcasts [obj] (an object serializable with kotlinx.serialization) on [channel] to all connected modded players */
	inline fun <reified T : Any> broadcastSerializableObject(channel: Channel, obj: T) {
		broadcastPacket(channel, Cbor.encodeToByteArray(obj))
	}

	/** Sends [content] on [channel] to [player] */
	fun sendPacket(channel: Channel, player: Player, packet: ByteArray) {
		player.sendPluginMessage(plugin, channel.bukkit, packet)
	}

	/** Sends [obj] (an object serializable with kotlinx.serialization) on [channel] to [player] */
	inline fun <reified T : Any> sendSerializableObject(channel: Channel, player: Player, obj: T) {
		sendPacket(channel, player, Cbor.encodeToByteArray(obj))
	}
}
