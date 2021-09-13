package io.github.petercrawley.minecraftstarshipplugin.screens

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.getPlugin
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.itemWithName
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

class InterfaceScreen(origin: Block, private val player: Player): Listener {
	private val screen = Bukkit.createInventory(player, InventoryType.HOPPER, Component.text("Starship Interface"))

	private val starship = Starship(origin, player)

	init {
		player.openInventory(screen)

		screen.setItem(0, itemWithName(Material.MINECART, "Detect Starship", 128, 255, 128, true, false))
		screen.setItem(1, itemWithName(Material.COMPASS, "Pilot Starship", 128, 128, 255, true, false))
		screen.setItem(4, itemWithName(Material.BEDROCK, "Allow Undetectables", 255, 128, 128, true, false))

		Bukkit.getPluginManager().registerEvents(this, getPlugin())
	}

	private fun unregister() {
		InventoryCloseEvent.getHandlerList().unregister(this)
		InventoryClickEvent.getHandlerList().unregister(this)
	}

	@EventHandler
	fun onPlayerCloseScreen(event: InventoryCloseEvent) {
		if (event.inventory == screen) unregister()
	}

	@EventHandler
	fun onPlayerClick(event: InventoryClickEvent) {
		if (event.inventory == screen) {
			when (event.rawSlot) {
				0 -> {
					starship.detect()
					screen.close()
					unregister()

				}
				1 -> {
					screen.close()
					unregister()
					player.sendMessage("Not implemented.")

				}
				4 -> {
					screen.close()
					unregister()
					player.sendMessage("Not implemented.")

				}
			}

			event.isCancelled = true
		}
	}
}