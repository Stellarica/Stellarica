package io.github.petercrawley.minecraftstarshipplugin.starships.screens

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.defaultUndetectable
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.itemWithName
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.itemWithTranslatableName
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.plugin
import io.github.petercrawley.minecraftstarshipplugin.starships.Starship
import io.github.petercrawley.minecraftstarshipplugin.utils.Screen
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.math.max
import kotlin.math.min

class AllowUndetectablesScreen(player: Player, private val starship: Starship) : Screen() {
	private val disallowed = defaultUndetectable.toMutableList()
	private val allowed = starship.allowedBlocks.toMutableList()

	private var topPage = 0
	private var bottomPage = 0

	private var topMaxPage = 0
	private var bottomMaxPage = 0

	private var topStart = 0
	private var bottomStart = 0

	// These are just here, so we don't keep recreating them
	private val red = ItemStack(Material.RED_STAINED_GLASS_PANE)
	private val green = ItemStack(Material.GREEN_STAINED_GLASS_PANE)
	private val gray = ItemStack(Material.GRAY_STAINED_GLASS_PANE)
	private val air = ItemStack(Material.AIR)
	private val last = itemWithName(Material.ARROW, "Previous Page", bold = true)
	private val next = itemWithName(Material.ARROW, "Next Page", bold = true)

	init {
		createScreen(player, 54, "Allow Undetectable Blocks")

		screen.setItem(1, red)
		screen.setItem(2, red)
		screen.setItem(3, red)
		screen.setItem(4, red)
		screen.setItem(5, red)
		screen.setItem(6, red)
		screen.setItem(7, red)
		screen.setItem(37, green)
		screen.setItem(38, green)
		screen.setItem(39, green)
		screen.setItem(40, green)
		screen.setItem(41, green)
		screen.setItem(42, green)
		screen.setItem(43, green)
	}

	override fun onScreenUpdate() {
		topMaxPage = (disallowed.size - 1) / 27
		bottomMaxPage = (allowed.size - 1) / 9

		topPage = max(min(topPage, topMaxPage), 0)
		bottomPage = max(min(bottomPage, bottomMaxPage), 0)

		topStart = topPage * 27
		bottomStart = bottomPage * 9

		if (disallowed.isEmpty()) {
			for (index in 9..35) screen.setItem(index, gray)
			screen.setItem(22, itemWithName(Material.BARRIER, "Undetectable list is empty!", 255, 0, 0, true))

		} else {
			for (index in topStart..topStart + 26) {
				val inventoryIndex = index - topStart + 9

				val material = disallowed.getOrNull(index)

				if (material == null) screen.setItem(inventoryIndex, air)
				else {
					val bukkitMaterial = material.getBukkit()

					if (bukkitMaterial == null) screen.setItem(
						inventoryIndex,
						itemWithName(Material.BARRIER, material.toString().replace("_", " ").replaceFirstChar { it.uppercaseChar() })
					)
					else if (!bukkitMaterial.isItem) screen.setItem(
						inventoryIndex,
						itemWithTranslatableName(Material.BARRIER, bukkitMaterial.translationKey)
					)
					else screen.setItem(
						inventoryIndex,
						itemWithTranslatableName(bukkitMaterial, bukkitMaterial.translationKey)
					)
				}
			}
		}

		if (allowed.isEmpty()) {
			for (index in 45..53) screen.setItem(index, gray)
			screen.setItem(49, itemWithName(Material.BARRIER, "Allowed undetectables list is empty!", 255, 0, 0, true))

		} else {
			for (index in bottomStart..bottomStart + 8) {
				val inventoryIndex = index - bottomStart + 45

				val material = allowed.getOrNull(index)

				if (material == null) screen.setItem(inventoryIndex, air)
				else {
					val bukkitMaterial = material.getBukkit()

					if (bukkitMaterial == null) screen.setItem(
						inventoryIndex,
						itemWithName(
							Material.BARRIER,
							material.toString().replace("_", " ").replaceFirstChar { it.uppercaseChar() })
					)
					else if (!bukkitMaterial.isItem) screen.setItem(
						inventoryIndex,
						itemWithTranslatableName(Material.BARRIER, bukkitMaterial.translationKey)
					)
					else screen.setItem(
						inventoryIndex,
						itemWithTranslatableName(bukkitMaterial, bukkitMaterial.translationKey)
					)
				}
			}
		}

		screen.setItem(0, if (topPage > 0) last else red)
		screen.setItem(8, if (topPage < topMaxPage) next else red)
		screen.setItem(36, if (bottomPage > 0) last else green)
		screen.setItem(44, if (bottomPage < bottomMaxPage) next else green)
	}

	override fun onScreenButtonClicked(slot: Int) {
		when (slot) {
			9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35 -> {
				val id = slot + topStart - 9
				if (id > disallowed.lastIndex) return
				allowed.add(disallowed.removeAt(id))
			}
			45, 46, 47, 48, 49, 50, 51, 52, 53 -> {
				val id = slot + bottomStart - 45
				if (id > allowed.lastIndex) return
				disallowed.add(allowed.removeAt(id))
			}
			0 -> topPage--
			8 -> topPage++
			36 -> bottomPage--
			44 -> bottomPage++
		}
	}

	override fun onScreenClosed() {
		starship.allowedBlocks = allowed.toMutableSet()
		Bukkit.getScheduler().runTask(plugin, Runnable { InterfaceScreen(player, starship) })
	}
}