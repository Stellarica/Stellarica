package io.github.petercrawley.minecraftstarshipplugin

import io.github.petercrawley.minecraftstarshipplugin.ships.Interface
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class MinecraftStarshipPlugin: JavaPlugin() {
	override fun onEnable() {
		Bukkit.getPluginManager().registerEvents(Interface(this), this)
	}
}