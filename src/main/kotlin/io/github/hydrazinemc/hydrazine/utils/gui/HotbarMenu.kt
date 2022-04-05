package io.github.hydrazinemc.hydrazine.utils.gui

import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.plugin
import io.github.hydrazinemc.hydrazine.utils.extensions.hotbar
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack

abstract class HotbarMenu: Listener {
	private val players = mutableMapOf<Player, HotbarState>()

	/**
	 * Whether the player can toggle between this and the normal hotbar
	 */
	abstract val toggleable: Boolean

	init {
		Bukkit.getPluginManager().registerEvents(this, plugin)
	}

	/**
	 * Called when the player selects the item at index
	 */
	open fun onButtonClicked(index: Int, player: Player) {}

	/**
	 * Called when the player changes their slot selection
	 */
	open fun onChangeSelectedSlot(oldIndex: Int, newIndex: Int, player: Player) {}

	/**
	 * Called when the menu is first opened
	 */
	open fun onMenuOpened(player: Player) {}

	/**
	 * Called before the menu closes
	 */
	open fun onMenuClosed(player: Player) {}

	/**
	 * Called when the player toggles between this and the normal hotbar.=
	 * @see toggleable
	 */
	open fun onMenuToggled(player: Player) {}


	fun openMenu(player: Player) {
		players[player] = HotbarState(player.hotbar, mutableListOf(), true)
		onMenuOpened(player)
	}

	fun closeMenu(player: Player) {
		val status = players[player] ?: return
		if (status.isMenuOpen) {
			player.hotbar = status.originalHotbar
		}
		onMenuClosed(player)
		players.remove(player)
	}

	@EventHandler
	fun onPlayerChangeSlot(event: PlayerItemHeldEvent) {
		onChangeSelectedSlot(event.previousSlot, event.newSlot, event.player)
	}

	@EventHandler
	fun onPlayerToggleMenu(event: PlayerDropItemEvent) {
		val status = players[event.player] ?: return
		if (status.isMenuOpen && !toggleable) {
			event.isCancelled = true
			return
		} // else warn?

		if (status.isMenuOpen) {
			// set the hotbar back to the original, store the menu hotbar
			status.menuHotbar = event.player.hotbar
			event.player.inventory.setItemInMainHand(null) // without this the item in the hand carries over
			event.player.hotbar = status.originalHotbar
		}
		else {
			// set the hotbar to the menu, store the original
			status.originalHotbar = event.player.hotbar
			event.player.inventory.setItemInMainHand(null)
			event.player.hotbar = status.menuHotbar
		}

		status.isMenuOpen = !status.isMenuOpen
		players[event.player] = status
		onMenuToggled(event.player)
		event.isCancelled = true
	}

	@EventHandler
	fun onPlayerLeave(event: PlayerQuitEvent) {
		if (event.player in players.keys) {
			closeMenu(event.player)
		}
	}

	@EventHandler
	fun onPlayerClick(event: PlayerInteractEvent) {
		val status = players[event.player] ?: return
		if (status.isMenuOpen) event.isCancelled = true
		onButtonClicked(event.player.inventory.heldItemSlot, event.player)
	}

	@EventHandler
	fun onInventoryClickEvent(event: InventoryClickEvent) {
		val status = players[event.whoClicked] ?: return
		if (status.isMenuOpen) event.isCancelled = true
	}

	@EventHandler
	fun onPlayerDragItemEvent(event: InventoryDragEvent) {
		val status = players[event.whoClicked] ?: return
		if (status.isMenuOpen) event.isCancelled = true
	}

}

data class HotbarState(var originalHotbar: MutableList<ItemStack?>, var menuHotbar: MutableList<ItemStack?>, var isMenuOpen: Boolean)