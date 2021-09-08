package io.github.petercrawley.minecraftstarshipplugin

import io.github.petercrawley.minecraftstarshipplugin.ships.Interface
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class MinecraftStarshipPlugin: JavaPlugin() {
	companion object {
		// I spent far too long trying to do this with kotlin getters and setters... I give up.
		private lateinit var plugin: MinecraftStarshipPlugin

		fun getPlugin(): MinecraftStarshipPlugin {
			return plugin
		}
	}

	override fun onEnable() {
		plugin = this
		plugin.saveDefaultConfig() // Save the default config, doesn't overwrite existing

		Bukkit.getPluginManager().registerEvents(Interface(), this)
	}
}