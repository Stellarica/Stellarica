package io.github.petercrawley.minecraftstarshipplugin

import io.github.petercrawley.minecraftstarshipplugin.commands.CommandTabComplete
import io.github.petercrawley.minecraftstarshipplugin.commands.MSPCommands
import io.github.petercrawley.minecraftstarshipplugin.ships.Interface
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin

class MinecraftStarshipPlugin: JavaPlugin() {
	companion object {
		// I spent far too long trying to do this with kotlin getters and setters... I give up.
		private lateinit var plugin: MinecraftStarshipPlugin

		fun getPlugin(): MinecraftStarshipPlugin {
			return plugin
		}
	}

	var nonDetectableBlocks: MutableSet<Material> = mutableSetOf()

	override fun onEnable() {
		plugin = this
		plugin.saveDefaultConfig() // Save the default config, doesn't overwrite existing
		plugin.updateNonDetectableBlocks()

		Bukkit.getPluginManager().registerEvents(Interface(), this)

		plugin.getCommand("msp")!!.setExecutor(MSPCommands())
		plugin.getCommand("msp")!!.tabCompleter = CommandTabComplete()
	}

	fun updateNonDetectableBlocks(){
		// Get the non-detectable blocks from the config file
		nonDetectableBlocks = mutableSetOf()
		config.getStringList("non-detectable-blocks").forEach {
			if (Material.getMaterial(it) == null){
				logger.warning("No Material for $it! Make sure all non-detectable blocks are correctly named!")
			}
			else {
				nonDetectableBlocks.add(Material.getMaterial(it)!!)
			}
		}
	}
}