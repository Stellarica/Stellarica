package io.github.petercrawley.minecraftstarshipplugin

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

	val nonDetectableBlocks: MutableSet<Material> = mutableSetOf(Material.AIR)

	override fun onEnable() {
		plugin = this
		plugin.saveDefaultConfig() // Save the default config, doesn't overwrite existing

		Bukkit.getPluginManager().registerEvents(Interface(), this)

		// Get the non-detectable blocks from the config file
		// TODO: Config reload command
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