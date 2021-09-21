package io.github.petercrawley.minecraftstarshipplugin.starships.screens

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.defaultUndetectable
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.getPlugin
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.itemWithName
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.itemWithTranslatableName
import io.github.petercrawley.minecraftstarshipplugin.Screen
import io.github.petercrawley.minecraftstarshipplugin.customblocks.MSPMaterial
import io.github.petercrawley.minecraftstarshipplugin.starships.Starship
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.math.max
import kotlin.math.min

class AllowUndetectableScreen(private val starship: Starship, player: Player): Screen(player, 54, "Allow Undetectables") {
	lateinit var disallowed: MutableList<MSPMaterial>
	lateinit var allowed: MutableList<MSPMaterial>

	var topPage = 0
	var bottomPage = 0

	var topMaxPage = 0
	var bottomMaxPage = 0

	var topStart = 0
	var bottomStart = 0

	override fun init() {
		disallowed = defaultUndetectable.toMutableList() // A list version, we have to store it here because it must retain its order
		allowed = starship.allowedBlocks.toMutableList()

		screen.setItem(1, ItemStack(Material.RED_STAINED_GLASS_PANE))
		screen.setItem(2, ItemStack(Material.RED_STAINED_GLASS_PANE))
		screen.setItem(3, ItemStack(Material.RED_STAINED_GLASS_PANE))
		screen.setItem(4, ItemStack(Material.RED_STAINED_GLASS_PANE))
		screen.setItem(5, ItemStack(Material.RED_STAINED_GLASS_PANE))
		screen.setItem(6, ItemStack(Material.RED_STAINED_GLASS_PANE))
		screen.setItem(7, ItemStack(Material.RED_STAINED_GLASS_PANE))
		screen.setItem(37, ItemStack(Material.GREEN_STAINED_GLASS_PANE))
		screen.setItem(38, ItemStack(Material.GREEN_STAINED_GLASS_PANE))
		screen.setItem(39, ItemStack(Material.GREEN_STAINED_GLASS_PANE))
		screen.setItem(40, ItemStack(Material.GREEN_STAINED_GLASS_PANE))
		screen.setItem(41, ItemStack(Material.GREEN_STAINED_GLASS_PANE))
		screen.setItem(42, ItemStack(Material.GREEN_STAINED_GLASS_PANE))
		screen.setItem(43, ItemStack(Material.GREEN_STAINED_GLASS_PANE))
	}

	override fun update() {
		topMaxPage = disallowed.size / 27
		bottomMaxPage = allowed.size / 9

		topPage = max(min(topPage, topMaxPage), 0)
		bottomPage = max(min(bottomPage, bottomMaxPage), 0)

		for (i in 9 .. 35) screen.clear(i)
		for (i in 45 .. 53) screen.clear(i)

		topStart = topPage * 27
		bottomStart = bottomPage * 9

		for (i in topStart .. min(topStart + 26, disallowed.lastIndex)) {
			val id = i - topStart + 9

			val bukkitMaterial = disallowed[i].getBukkit()

			if (bukkitMaterial == null) screen.setItem(id, itemWithName(Material.BARRIER, (disallowed[i].get() as String).replace("_", " ").replaceFirstChar { it.uppercaseChar() }))
			else if (!bukkitMaterial.isItem) screen.setItem(id, itemWithTranslatableName(Material.BARRIER, bukkitMaterial.translationKey))
			else screen.setItem(id, itemWithTranslatableName(bukkitMaterial, bukkitMaterial.translationKey))
		}

		for (i in bottomStart .. min(bottomStart + 8, allowed.lastIndex)) {
			val id = i - bottomStart + 45

			val bukkitMaterial = allowed[i].getBukkit()

			if (bukkitMaterial == null) screen.setItem(id, itemWithName(Material.BARRIER, (allowed[i].get() as String).replace("_", " ").replaceFirstChar { it.uppercaseChar() }))
			else if (!bukkitMaterial.isItem) screen.setItem(id, itemWithTranslatableName(Material.BARRIER, bukkitMaterial.translationKey))
			else screen.setItem(id, itemWithTranslatableName(bukkitMaterial, bukkitMaterial.translationKey))
		}

		// Oh boy, this is A LOT of setitem() calls.
		// TODO: Maybe there is a less stupid way of doing this?
		screen.setItem(0, if (topPage > 0) itemWithName(Material.ARROW, "Previous Page", bold = true) else ItemStack(Material.RED_STAINED_GLASS_PANE))
		screen.setItem(8, if (topPage < topMaxPage) itemWithName(Material.ARROW, "Next Page", bold = true) else ItemStack(Material.RED_STAINED_GLASS_PANE))
		screen.setItem(36, if (bottomPage > 0) itemWithName(Material.ARROW, "Previous Page", bold = true) else ItemStack(Material.GREEN_STAINED_GLASS_PANE))
		screen.setItem(44, if (bottomPage < bottomMaxPage) itemWithName(Material.ARROW, "Next Page", bold = true) else ItemStack(Material.GREEN_STAINED_GLASS_PANE))
	}

	override fun slotClicked(slot: Int) {
		when (slot) {
			0 -> topPage--
			8 -> topPage++
			36 -> bottomPage--
			44 -> bottomPage++
			9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35 -> {
				val id = slot + topStart - 9

				allowed.add(disallowed[id])
				disallowed.removeAt(id)
			}
			45, 46, 47, 48, 49, 50, 51, 52, 53 -> {
				val id = slot + bottomStart - 45

				disallowed.add(allowed[id])
				allowed.removeAt(id)
			}
		}
	}

	override fun closed() {
		starship.allowedBlocks = allowed.toSet() as MutableSet<MSPMaterial>

		Bukkit.getScheduler().runTask(getPlugin(), Runnable {InterfaceScreen(starship, player)})
	}
}