package io.github.petercrawley.minecraftstarshipplugin.screens

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.getPlugin
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.itemWithName
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.itemWithTranslatableName
import io.github.petercrawley.minecraftstarshipplugin.Starship
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.max
import kotlin.math.min

class AllowUndetectableScreen(private val starship: Starship, private val player: Player): Listener {
	private val screen = Bukkit.createInventory(player, 54, Component.text("Allow Undetectables"))

	private val undetectableItems: MutableList<Material> = starship.getCustomisedUndetectables()

	private var page: Int = 0
	private val maxPage = undetectableItems.size / 45

	private fun displayPage() {
		page = max(min(page, maxPage), 0)

		for (i in 0 .. 53) {
			screen.clear(i)
		}

		val start = page * 45
		val end = start + 44

		for (i in start .. min(end, undetectableItems.lastIndex)) {
			var item = undetectableItems[i]

			if (!item.isItem) {
				item = Material.BARRIER
			}

			screen.setItem(i - start, itemWithTranslatableName(item, undetectableItems[i].translationKey()))
		}

		screen.setItem(45, if (page > 0) itemWithName(Material.ARROW, "Previous Page", bold = true) else ItemStack(Material.BLACK_STAINED_GLASS_PANE))
		screen.setItem(46, ItemStack(Material.BLACK_STAINED_GLASS_PANE))
		screen.setItem(47, ItemStack(Material.BLACK_STAINED_GLASS_PANE))
		screen.setItem(48, ItemStack(Material.BLACK_STAINED_GLASS_PANE))
		screen.setItem(49, ItemStack(Material.BLACK_STAINED_GLASS_PANE))
		screen.setItem(50, ItemStack(Material.BLACK_STAINED_GLASS_PANE))
		screen.setItem(51, ItemStack(Material.BLACK_STAINED_GLASS_PANE))
		screen.setItem(52, ItemStack(Material.BLACK_STAINED_GLASS_PANE))
		screen.setItem(53, if (page < maxPage) itemWithName(Material.ARROW, "Next Page", bold = true) else ItemStack(Material.BLACK_STAINED_GLASS_PANE))
	}

	init {
		player.openInventory(screen)

		displayPage()

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
				45 -> {
					if (page > 0) page--
					displayPage()
				}
				53 -> {
					if (page < maxPage) page++
					displayPage()
				}
			}

			event.isCancelled = true
		}
	}

}