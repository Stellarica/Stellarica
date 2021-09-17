package io.github.petercrawley.minecraftstarshipplugin.starships.screens

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.getPlugin
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.itemWithName
import io.github.petercrawley.minecraftstarshipplugin.starships.Starship
import io.github.petercrawley.minecraftstarshipplugin.starships.StarshipManager.activateStarship
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType

class InterfaceScreen(private val starship: Starship): Listener {
	private val screen = Bukkit.createInventory(starship.owner, InventoryType.HOPPER, Component.text("Starship Interface"))

	init {
		screen.setItem(0, itemWithName(Material.MINECART, "Detect Starship", 128, 255, 128, bold = true))
		screen.setItem(1, itemWithName(Material.COMPASS, "Pilot Starship", 128, 128, 255, bold = true))
		screen.setItem(4, itemWithName(Material.BEDROCK, "Allow Undetectables", 255, 128, 128, bold = true))

		starship.owner.openInventory(screen)

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
				0 -> starship.detect()
				1 -> {
					activateStarship(starship, starship.owner)
					screen.close()
					unregister()

				}
				4 -> {
					AllowUndetectableScreen(starship)
					screen.close()
					unregister()

				}
			}

			event.isCancelled = true
		}
	}
}