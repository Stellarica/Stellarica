package io.github.petercrawley.minecraftstarshipplugin.starships

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.getPlugin
import net.kyori.adventure.text.Component.text
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory

abstract class Screen: Listener {
	lateinit var screen: Inventory
	private set

	private fun initScreen() {
		onScreenUpdate()
		Bukkit.getPluginManager().registerEvents(this, getPlugin())
	}
	
	fun createScreen(player: Player, type: InventoryType, name: String) {
		screen = Bukkit.createInventory(player, type, text(name))
		initScreen()
	}

	fun createScreen(player: Player, size: Int, name: String) {
		screen = Bukkit.createInventory(player, size, text(name))
		initScreen()
	}

	fun onScreenUpdate() {}

	fun onScreenButtonClicked(slot: Int) {}

	fun onScreenClosed() {}

	fun closeScreen() {
		// Unregister handlers first, otherwise we will create a loop when we call screen.close()
		InventoryCloseEvent.getHandlerList().unregister(this)
		InventoryClickEvent.getHandlerList().unregister(this)

		screen.close()

		onScreenClosed()
	}
	
	@EventHandler fun onInventoryClickEvent(event: InventoryClickEvent) {
		if (event.inventory == screen) {
			event.isCancelled = true
			onScreenButtonClicked(event.rawSlot)
			onScreenUpdate()
		}
	}
	
	@EventHandler fun onPlayerCloseScreenEvent(event: InventoryCloseEvent) {
		if (event.inventory == screen) closeScreen()
	}
}