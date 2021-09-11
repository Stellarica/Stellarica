package io.github.petercrawley.minecraftstarshipplugin

import io.github.petercrawley.minecraftstarshipplugin.commands.CommandTabComplete
import io.github.petercrawley.minecraftstarshipplugin.commands.Commands
import io.github.petercrawley.minecraftstarshipplugin.ships.Interface
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.*
import org.bukkit.plugin.RegisteredListener
import org.bukkit.plugin.java.JavaPlugin
import org.hjson.JsonValue
import java.io.File

class MinecraftStarshipPlugin: JavaPlugin() {
	companion object {
		// I spent far too long trying to do this with kotlin getters and setters... I give up.
		private lateinit var plugin: MinecraftStarshipPlugin

		fun getPlugin(): MinecraftStarshipPlugin {
			return plugin
		}
	}

	var nonDetectableBlocks: MutableSet<Material> = mutableSetOf()

	override fun saveDefaultConfig() {
		plugin.saveResource("config.hjson", false)
	}

	override fun onEnable() {
		plugin = this
		plugin.saveDefaultConfig()
		plugin.updateNonDetectableBlocks()

		Bukkit.getPluginManager().registerEvents(Interface(), this)
		Bukkit.getPluginManager().registerEvents(CustomBlocks(), this)

		plugin.getCommand("msp")!!.setExecutor(Commands())
		plugin.getCommand("msp")!!.tabCompleter = CommandTabComplete()
	}

	fun updateNonDetectableBlocks(){
		// Get the non-detectable blocks from the config file
		nonDetectableBlocks = mutableSetOf()

		JsonValue.readHjson(File(plugin.dataFolder, "config.hjson").bufferedReader()).asObject()["non-detectable-blocks"].asArray().forEach {
			val value = it.asString()

			if (Material.getMaterial(value) == null){
				logger.warning("No Material for $it! Make sure all non-detectable blocks are correctly named!")
			}
			else {
				nonDetectableBlocks.add(Material.getMaterial(value)!!)
			}
		}
	}
}