package io.github.petercrawley.minecraftstarshipplugin.interfaces

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.getPlugin
import io.github.petercrawley.minecraftstarshipplugin.Starship
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack

class Interface(origin: Block, player: Player): Listener {
	private val screen = Bukkit.createInventory(player, InventoryType.HOPPER, Component.text("Starship Interface"))

	private val starship = Starship(origin, player)

	init {
		player.openInventory(screen)

		val detect = ItemStack(Material.MINECART)
//		val pilot = ItemStack(Material.COMPASS)
//		val allow = ItemStack(Material.BEDROCK)

		val detectItemMeta = detect.itemMeta
//		val pilotItemMeta = pilot.itemMeta
//		val allowItemMeta = allow.itemMeta

		detectItemMeta.displayName(Component.text("Detect Starship").style(Style.style(TextColor.color(128, 255, 128)).decoration(TextDecoration.ITALIC, false).decorate(TextDecoration.BOLD)))
//		pilotItemMeta.displayName(Component.text("Pilot Starship").style(Style.style(TextColor.color(128, 128, 255)).decoration(TextDecoration.ITALIC, false).decorate(TextDecoration.BOLD)))
//		allowItemMeta.displayName(Component.text("Allow Undetectable").style(Style.style(TextColor.color(255, 128, 128)).decoration(TextDecoration.ITALIC, false).decorate(TextDecoration.BOLD)))

		detect.itemMeta = detectItemMeta
//		pilot.itemMeta = pilotItemMeta
//		allow.itemMeta = allowItemMeta

		screen.setItem(0, detect)
//		screen.setItem(1, pilot)
//		screen.setItem(4, allow)

		Bukkit.getPluginManager().registerEvents(this, getPlugin())
	}

	private fun unregister() {
		InventoryCloseEvent.getHandlerList().unregister(this)
		InventoryClickEvent.getHandlerList().unregister(this)
	}

	@EventHandler
	fun onPlayerCloseScreen(event: InventoryCloseEvent) {
		if (event.inventory == screen) {
			unregister()
		}
	}

	@EventHandler
	fun onPlayerClick(event: InventoryClickEvent) {
		if (event.inventory == screen) {
			if (event.rawSlot == 0) {
				starship.detect()
				screen.close()
				unregister()
			}

			event.isCancelled = true
		}
	}
}