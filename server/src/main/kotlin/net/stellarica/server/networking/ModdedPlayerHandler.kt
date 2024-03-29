package net.stellarica.server.networking

import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.minecraft.resources.ResourceLocation
import net.stellarica.common.networking.Channel
import net.stellarica.common.networking.ClientCustomItemData
import net.stellarica.common.networking.networkVersion
import net.stellarica.server.CustomItems
import net.stellarica.server.StellaricaServer.Companion.klogger
import net.stellarica.server.material.block.type.BlockType
import net.stellarica.server.material.item.type.ItemType
import net.stellarica.server.util.Tasks
import net.stellarica.server.util.asMiniMessage
import org.bukkit.GameMode
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCreativeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object ModdedPlayerHandler : Listener {
	val networkHandler = BukkitNetworkHandler()
	val moddedPlayers = mutableSetOf<Player>()

	private val creativeItems = CustomItems.values.map {
		ClientCustomItemData(
			it.id,
			it.base.getId(),
			it.modelData,
			it.name.toJsonText
		)
	}

	init {
		ServerboundPacketListener(channel = Channel.ITEM_SYNC) { player, _ ->
			if (player.gameMode == GameMode.CREATIVE) {
				val target = player.getTargetBlockExact(10) ?: return@ServerboundPacketListener false
				val type = BlockType.of(target).getItem()
				if (type?.isCustom == true) {
					player.inventory.setItemInMainHand(type.getBukkitItemStack(1))
				}
			}
			return@ServerboundPacketListener false
		}.register()
	}

	@EventHandler
	private fun onPlayerJoin(event: PlayerJoinEvent) {
		Tasks.syncDelay(20) {
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
	private fun onPlayerLeave(event: PlayerQuitEvent) {
		moddedPlayers.remove(event.player)
	}

	@EventHandler
	private fun onCreativeInventoryClick(event: InventoryCreativeEvent) {
		if (event.whoClicked !in moddedPlayers) return

		// See https://wiki.vg/Protocol#Set_Creative_Mode_Slot
		//
		// Because the creative menu is mostly client side, replace the item the client spawns in
		// with the actual custom item whenever they go to use it. This saves us from having to send
		// all the custom item data to the client (to have it construct the item itself) even
		// though it is a bit of a duct tape solution
		val id = CraftItemStack.asNMSCopy(event.cursor).orCreateTag.getString("client_custom_item")
		if (id.isNullOrEmpty()) return

		val type = ItemType.of(CustomItems[ResourceLocation.tryParse(id)!!]!!)
		event.cursor = type.getBukkitItemStack(event.cursor.amount)
	}

	private fun handleModdedPlayerJoin(player: Player) {
		klogger.info { "Handling modded player sync for ${player.name}" }
		networkHandler.sendSerializableObject(Channel.ITEM_SYNC, player, creativeItems)
	}

	private val String.toJsonText: String
		get() = GsonComponentSerializer.gson().serialize(this.asMiniMessage)
}
