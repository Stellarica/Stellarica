package net.stellarica.server.utils.gui.hotbar

import net.stellarica.server.StellaricaServer.Companion.plugin
import net.stellarica.server.utils.Tasks
import net.stellarica.server.utils.extensions.hotbar
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerQuitEvent

/**
 * Abstract Hotbar menu
 */
// TODO: handle opening menu when another menu is open
abstract class HotbarMenu : Listener {
	private val players = mutableMapOf<Player, HotbarState>()

	/**
	 * Whether the player can toggle between this and the normal hotbar
	 */
	abstract val toggleable: Boolean

	init {
		Bukkit.getPluginManager().registerEvents(this, plugin)
	}

	//region hooks
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
	//endregion

	/**
	 * Open this menu for [player]
	 */
	fun openMenu(player: Player) {
		players[player] = HotbarState(player.hotbar, mutableListOf(), true)
		onMenuOpened(player)
	}

	/**
	 * Close this menu for [player]
	 */
	fun closeMenu(player: Player) {
		val status = players[player] ?: return
		if (status.isMenuOpen) {
			player.hotbar = status.originalHotbar
		}
		onMenuClosed(player)
		players.remove(player)
	}

	//region handlers
	/**
	 * Event handler, do not call
	 */
	@EventHandler
	fun onPlayerChangeSlot(event: PlayerItemHeldEvent) {
		onChangeSelectedSlot(event.previousSlot, event.newSlot, event.player)
	}


	/**
	 * Event handler, do not call
	 */
	@EventHandler
	fun onPlayerToggleMenu(event: PlayerDropItemEvent) {
		val status = players[event.player] ?: return
		if (status.isMenuOpen && !toggleable) {
			event.isCancelled = true
			return
		} // else warn?
		event.isCancelled = true

		// Without a delay here, the player's held item overwrites the new hotbar
		// Do I know why? No.
		Tasks.syncDelay(1) {
			if (status.isMenuOpen) {
				// set the hotbar back to the original, store the menu hotbar
				status.menuHotbar = event.player.hotbar
				event.player.hotbar = status.originalHotbar
			} else {
				// set the hotbar to the menu, store the original
				status.originalHotbar = event.player.hotbar
				event.player.hotbar = status.menuHotbar
			}

			status.isMenuOpen = !status.isMenuOpen
			players[event.player] = status

			onMenuToggled(event.player)
		}
	}

	/**
	 * Event handler, do not call
	 */
	@EventHandler
	fun onPlayerClick(event: PlayerInteractEvent) {
		val status = players[event.player] ?: return
		if (status.isMenuOpen) event.isCancelled = true
		onButtonClicked(event.player.inventory.heldItemSlot, event.player)
	}


	/**
	 * Event handler, do not call
	 */
	@EventHandler
	fun onInventoryClick(event: InventoryClickEvent) {
		val status = players[event.whoClicked] ?: return
		if (status.isMenuOpen) event.isCancelled = true
	}

	/**
	 * Event handler, do not call
	 */
	@EventHandler
	fun onPlayerDragItem(event: InventoryDragEvent) {
		val status = players[event.whoClicked] ?: return
		if (status.isMenuOpen) event.isCancelled = true
	}

	/**
	 * Event handler, do not call
	 */
	@EventHandler
	fun onPlayerDragItem(event: PlayerAttemptPickupItemEvent) {
		val status = players[event.player] ?: return
		if (status.isMenuOpen) event.isCancelled = true
	}

	/**
	 * Event handler, do not call
	 */
	@EventHandler
	fun onPlayerDie(event: PlayerDeathEvent) {
		players[event.player] ?: return
		closeMenu(event.player)
	}

	/**
	 * Event handler, do not call
	 */
	@EventHandler
	fun onPlayerLeave(event: PlayerQuitEvent) {
		players[event.player] ?: return
		closeMenu(event.player)
	}
	//endregion
}
