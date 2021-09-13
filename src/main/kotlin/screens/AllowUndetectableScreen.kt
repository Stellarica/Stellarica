package io.github.petercrawley.minecraftstarshipplugin.screens

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin
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

	private val defaultUndetectable = MinecraftStarshipPlugin.defaultUndetectable.toList() // A list version, we have to store it here because it must retain its order

	private var leftPage = 0
	private var rightPage = 0

	private var leftMaxPage = defaultUndetectable.size / 24
	private var rightMaxPage = starship.allowedBlocks.size / 8

	private fun update() {
		leftMaxPage = defaultUndetectable.size / 24
		rightMaxPage = starship.allowedBlocks.size / 8

		leftPage = max(min(leftPage, leftMaxPage), 0)
		rightPage = max(min(rightPage, rightMaxPage), 0)

		screen.clear()

		val start = leftPage * 24

		for (i in start .. min(start + 23, defaultUndetectable.lastIndex)) {
			var id = 9
//			if (id == 15 || id == 24 || id == 33) id += 3
			id -= start

			var bukkitMaterial = defaultUndetectable[i].getBukkit()

			if (bukkitMaterial == null) {
				screen.setItem(id, itemWithName(Material.BARRIER, (defaultUndetectable[i].get() as String).replace("_", " ").replaceFirstChar { it.uppercaseChar() }))
			} else if (!bukkitMaterial.isItem) {
				screen.setItem(id, itemWithTranslatableName(Material.BARRIER, bukkitMaterial.translationKey()))
			} else {
				screen.setItem(id, itemWithTranslatableName(bukkitMaterial, bukkitMaterial.translationKey()))
			}
		}

//		page = max(min(page, maxPage), 0)
//
//		for (i in 0 .. 53) {
//			screen.clear(i)
//		}
//
//		val start = page * 45
//		val end = start + 44
//
//		for (i in start .. min(end, undetectableItems.lastIndex)) {
//			var item = undetectableItems[i]
//
//			if (!item.isItem) {
//				item = Material.BARRIER
//			}
//
//			screen.setItem(i - start, itemWithTranslatableName(item, undetectableItems[i].translationKey()))
//		}
//
		screen.setItem(45, if (leftPage > 0) itemWithName(Material.ARROW, "Previous Page", bold = true) else ItemStack(Material.BLACK_STAINED_GLASS_PANE))
//		screen.setItem(46, ItemStack(Material.BLACK_STAINED_GLASS_PANE))
		screen.setItem(50, if (leftPage < leftMaxPage) itemWithName(Material.ARROW, "Next Page", bold = true) else ItemStack(Material.BLACK_STAINED_GLASS_PANE))
	}

	init {
		player.openInventory(screen)

		update()

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
					if (leftPage > 0) leftPage--
					update()
				}
				50 -> {
					if (leftPage < leftMaxPage) leftPage++
					update()
				}
			}

			event.isCancelled = true
		}
	}

}