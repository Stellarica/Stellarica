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
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import kotlin.math.max
import kotlin.math.min

class AllowUndetectableScreen(private val starship: Starship, private val player: Player): Listener {
    private val screen = Bukkit.createInventory(player, 54, Component.text("Allow Undetectables"))

    private val undetectableItems: MutableList<Material> = starship.getCustomisedUndetectables()

    private var page: Int = 0

    private fun displayPage() {
        page = max(min(page, undetectableItems.size / 45), 0)
        
        for (i in 0 .. 53) {
            screen.clear(i)
        }

        val start = page * 45
        val end = start + 44

        for (i in start .. min(end, undetectableItems.lastIndex)) {
            screen.setItem(i - start, itemWithName(undetectableItems[i], undetectableItems[i].name, italic = false))
        }

        screen.setItem(45, itemWithName(Material.ARROW, "Previous Page", bold = true))
        screen.setItem(53, itemWithName(Material.ARROW, "Next Page", bold = true))
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
                    page--
                    displayPage()
                }
                53 -> {
                    page++
                    displayPage()
                }
            }

            event.isCancelled = true
        }
    }

}