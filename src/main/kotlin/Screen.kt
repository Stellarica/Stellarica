@file:Suppress("LeakingThis")

package io.github.petercrawley.minecraftstarshipplugin

import net.kyori.adventure.text.Component.text
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory

abstract class Screen(val screen: Inventory, val player: Player): Listener {
	constructor(player: Player, size: Int, name: String) : this(Bukkit.createInventory(player, size, text(name)), player)

	constructor(player: Player, type: InventoryType, name: String) : this(Bukkit.createInventory(player, type, text(name)), player)

	init {
		update()

		player.openInventory(screen)

		Bukkit.getPluginManager().registerEvents(this, MinecraftStarshipPlugin.getPlugin())
	}

	abstract fun update()

	abstract fun slotClicked(slot: Int)

	abstract fun closed()

	fun close() {
		closed()
		unregister()
	}

	private fun unregister() {
		InventoryCloseEvent.getHandlerList().unregister(this)
		InventoryClickEvent.getHandlerList().unregister(this)
	}

	@EventHandler
	private fun onPlayerClick(event: InventoryClickEvent) {
		if (event.inventory == screen) {
			slotClicked(event.rawSlot)
			update()
			event.isCancelled = true
		}
	}

	@EventHandler
	private fun onPlayerCloseScreen(event: InventoryCloseEvent) {
		if (event.inventory == screen) {
			closed()
			unregister()
		}
	}
}