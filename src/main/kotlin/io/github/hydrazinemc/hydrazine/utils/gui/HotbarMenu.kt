package io.github.hydrazinemc.hydrazine.utils.gui

import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.plugin
import io.github.hydrazinemc.hydrazine.utils.extensions.hotbar
import io.github.hydrazinemc.hydrazine.utils.extensions.setHotbar
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack

abstract class HotbarMenu(player: Player): Listener {
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
	open fun onButtonClicked(index: Int) {}

	/**
	 * Called when the menu is first opened
	 */
	open fun onMenuOpened() {}

	/**
	 * Called before the menu closes
	 */
	open fun onMenuClosed() {}

	/**
	 * Called when the player toggles between this and the normal hotbar.=
	 * @see toggleable
	 */
	open fun onMenuToggled() {

	}


	fun openMenu(player: Player) {
		onMenuOpened()
	}

	fun closeMenu(player: Player) {
		val status = players[player]!!
		if (status.menuOpen) {
			player.setHotbar(status.originalHotbar)
		}
		onMenuClosed()
		players.remove(player)
	}

	@EventHandler
	fun onPlayerChangeSlot(event: PlayerItemHeldEvent) {
		onButtonClicked(event.newSlot)
	}

	@EventHandler
	fun onPlayerToggleMenu(event: PlayerDropItemEvent) {
		val status = players[event.player]!!
		if (status.menuOpen && !toggleable) {
			event.isCancelled = true
			return
		}

		if (status.menuOpen) {
			// set the hotbar back to the original, store the menu hotbar
			status.menuHotbar = event.player.hotbar
			event.player.setHotbar(status.originalHotbar)
		}
		else {
			// set the hotbar to the menu, store the original
			status.originalHotbar = event.player.hotbar
			event.player.setHotbar(status.menuHotbar)
		}

		status.menuOpen = !status.menuOpen
		players[event.player] = status
		event.player.setHotbar(if (status.menuOpen) {status.menuHotbar} else {status.originalHotbar})
		onMenuToggled()
	}

	@EventHandler
	fun onPlayerLeave(event: PlayerQuitEvent) {
		if (event.player in players.keys) {
			closeMenu(event.player)
		}
	}
}

data class HotbarState(var originalHotbar: MutableList<ItemStack?>, var menuHotbar: MutableList<ItemStack?>, var menuOpen: Boolean)