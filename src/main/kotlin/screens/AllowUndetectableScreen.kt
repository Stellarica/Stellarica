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
        val end = min(start + 23, defaultUndetectable.lastIndex)

		for (i in start .. end) {
			var id = i + 9
			if (id == 15 || id == 24 || id == 33) id += 3
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

		// Oh boy, this is A LOT of setitem() calls.
		// TODO: Maybe there is a less stupid way of doing this?
		screen.setItem(0, ItemStack(Material.RED_STAINED_GLASS_PANE))
		screen.setItem(1, ItemStack(Material.RED_STAINED_GLASS_PANE))
		screen.setItem(2, ItemStack(Material.RED_STAINED_GLASS_PANE))
		screen.setItem(3, ItemStack(Material.RED_STAINED_GLASS_PANE))
		screen.setItem(4, ItemStack(Material.RED_STAINED_GLASS_PANE))
		screen.setItem(5, ItemStack(Material.RED_STAINED_GLASS_PANE))
		screen.setItem(6, ItemStack(Material.BLACK_STAINED_GLASS_PANE))
		screen.setItem(7, ItemStack(Material.GREEN_STAINED_GLASS_PANE))
		screen.setItem(8, ItemStack(Material.GREEN_STAINED_GLASS_PANE))
		screen.setItem(15, ItemStack(Material.BLACK_STAINED_GLASS_PANE))
		screen.setItem(24, ItemStack(Material.BLACK_STAINED_GLASS_PANE))
		screen.setItem(33, ItemStack(Material.BLACK_STAINED_GLASS_PANE))
		screen.setItem(42, ItemStack(Material.BLACK_STAINED_GLASS_PANE))
		screen.setItem(45, if (leftPage > 0) itemWithName(Material.ARROW, "Previous Page", bold = true) else ItemStack(Material.BLACK_STAINED_GLASS_PANE))
		screen.setItem(46, ItemStack(Material.BLACK_STAINED_GLASS_PANE))
		screen.setItem(47, ItemStack(Material.BLACK_STAINED_GLASS_PANE))
		screen.setItem(48, ItemStack(Material.BLACK_STAINED_GLASS_PANE))
		screen.setItem(49, ItemStack(Material.BLACK_STAINED_GLASS_PANE))
		screen.setItem(50, if (leftPage < leftMaxPage) itemWithName(Material.ARROW, "Next Page", bold = true) else ItemStack(Material.BLACK_STAINED_GLASS_PANE))
		screen.setItem(51, ItemStack(Material.BLACK_STAINED_GLASS_PANE))
		screen.setItem(52, if (rightPage > 0) itemWithName(Material.ARROW, "Previous Page", bold = true) else ItemStack(Material.BLACK_STAINED_GLASS_PANE))
		screen.setItem(53, if (leftPage < leftMaxPage) itemWithName(Material.ARROW, "Next Page", bold = true) else ItemStack(Material.BLACK_STAINED_GLASS_PANE))
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