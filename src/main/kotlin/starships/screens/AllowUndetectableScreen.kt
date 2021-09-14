package io.github.petercrawley.minecraftstarshipplugin.starships.screens

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.getPlugin
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.itemWithName
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.itemWithTranslatableName
import io.github.petercrawley.minecraftstarshipplugin.starships.Starship
import io.github.petercrawley.minecraftstarshipplugin.customblocks.MSPMaterial
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.max
import kotlin.math.min

class AllowUndetectableScreen(private val starship: Starship): Listener {
	private val screen = Bukkit.createInventory(starship.owner, 54, Component.text("Allow Undetectables"))

	private val disallowed = MinecraftStarshipPlugin.defaultUndetectable.toMutableList() // A list version, we have to store it here because it must retain its order
	private val allowed = mutableListOf<MSPMaterial>()

	private var leftPage = 0
	private var rightPage = 0

	private var leftMaxPage = disallowed.size / 24
	private var rightMaxPage = allowed.size / 8

	private var leftStart = 0
	private var rightStart = 0
	private var leftEnd = 0
	private var rightEnd = 0

	private fun update() {
		leftMaxPage = disallowed.size / 24
		rightMaxPage = allowed.size / 8

		leftPage = max(min(leftPage, leftMaxPage), 0)
		rightPage = max(min(rightPage, rightMaxPage), 0)

		screen.clear()

		leftStart = leftPage * 24
        leftEnd = min(leftStart + 23, disallowed.lastIndex)

		rightStart = rightPage * 8
		rightEnd = min(rightStart + 7, allowed.lastIndex)

		for (i in leftStart .. leftEnd) {
			var id = i + 9
			id -= leftStart
			if (id == 15 || id == 24 || id == 33) id += 3

			val bukkitMaterial = disallowed[i].getBukkit()

			if (bukkitMaterial == null) {
				screen.setItem(id, itemWithName(Material.BARRIER, (disallowed[i].get() as String).replace("_", " ").replaceFirstChar { it.uppercaseChar() }))
			} else if (!bukkitMaterial.isItem) {
				screen.setItem(id, itemWithTranslatableName(Material.BARRIER, bukkitMaterial.translationKey()))
			} else {
				screen.setItem(id, itemWithTranslatableName(bukkitMaterial, bukkitMaterial.translationKey()))
			}
		}

		for (i in rightStart .. rightEnd) {
			var id = i + 15
			id -= rightStart
			if (id == 18 || id == 27 || id == 36) id += 6

			val bukkitMaterial = allowed[i].getBukkit()

			if (bukkitMaterial == null) {
				screen.setItem(id, itemWithName(Material.BARRIER, (allowed[i].get() as String).replace("_", " ").replaceFirstChar { it.uppercaseChar() }))
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
		starship.owner.openInventory(screen)

		update()

		Bukkit.getPluginManager().registerEvents(this, getPlugin())
	}

	private fun unregister() {
		InventoryCloseEvent.getHandlerList().unregister(this)
		InventoryClickEvent.getHandlerList().unregister(this)
	}

	@EventHandler
	fun onPlayerCloseScreen(event: InventoryCloseEvent) {
		if (event.inventory == screen) {
			InterfaceScreen(starship)
			unregister()
		}
	}

	@EventHandler
	fun onPlayerClick(event: InventoryClickEvent) {
		if (event.inventory == screen) {
			when (event.rawSlot) {
				45 -> leftPage--
				50 -> leftPage++
				52 -> rightPage--
				53 -> rightPage++
				9, 10, 11, 12, 13, 14, 18, 19, 20, 21, 22, 23, 27, 28, 29, 30, 31, 32, 36, 37, 38, 39, 40, 41 -> {
					var id = event.rawSlot
					if (id == 15 || id == 24 || id == 33) id -= 3
					id += leftStart
					id -= 9

					allowed.add(disallowed[id])
					disallowed.removeAt(id)
				}
			}

			update()

			event.isCancelled = true
		}
	}

}