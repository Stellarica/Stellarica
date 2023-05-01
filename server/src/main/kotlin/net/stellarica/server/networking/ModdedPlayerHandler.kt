package net.stellarica.server.networking

import net.minecraft.resources.ResourceLocation
import net.stellarica.common.networking.Channel
import net.stellarica.common.networking.ClientCustomItemData
import net.stellarica.common.networking.networkVersion
import net.stellarica.server.StellaricaServer.Companion.klogger
import net.stellarica.server.material.custom.item.CustomItems
import net.stellarica.server.material.type.item.ItemType
import net.stellarica.server.util.Tasks
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCreativeEvent
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
				if (content.first() == networkVersion) {
					moddedPlayers.add(event.player)
					handleModdedPlayerJoin(event.player)
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

	@EventHandler
	fun onCreativeInventoryClick(event: InventoryCreativeEvent) {
		if (event.whoClicked !in moddedPlayers) return

		// See https://wiki.vg/Protocol#Set_Creative_Mode_Slot
		//
		// Because the creative menu is mostly client side, replace the item the client spawns in
		// with the actual custom item whenever they go to use it. This saves us from having to send
		// all the custom item data to the client (to have it construct the item itself) even
		// though it is a bit of a duct tape solution

		val id = CraftItemStack.asNMSCopy(event.cursor).orCreateTag.getString("client_custom_item")
		if (id.isNullOrEmpty()) return

		val type = ItemType.of(CustomItems.byId(ResourceLocation.tryParse(id)!!)!!)
		event.cursor = type.getBukkitItemStack(event.cursor.amount)
	}

	private fun handleModdedPlayerJoin(player: Player) {
		klogger.info { "Handling modded player sync for ${player.name}" }
		val items = CustomItems.all().map { ClientCustomItemData(it.id, it.base.getId(), it.modelData) }
		networkHandler.sendSerializableObject(Channel.ITEM_SYNC, player, items)
	}
}