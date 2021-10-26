package io.github.petercrawley.minecraftstarshipplugin

import io.github.petercrawley.minecraftstarshipplugin.commands.CommandTabComplete
import io.github.petercrawley.minecraftstarshipplugin.commands.Commands
import io.github.petercrawley.minecraftstarshipplugin.customblocks.CustomBlocksListener
import io.github.petercrawley.minecraftstarshipplugin.customblocks.MSPMaterial
import io.github.petercrawley.minecraftstarshipplugin.starships.InterfaceListener
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class MinecraftStarshipPlugin : JavaPlugin() {
	companion object {
		lateinit var plugin: MinecraftStarshipPlugin
			private set

		var timeOperations: Boolean = false
			private set

		var detectionLimit: Int = 500000
			private set

		var forcedUndetectable = setOf<MSPMaterial>()
			private set

		var defaultUndetectable = setOf<MSPMaterial>()
			private set
	}

	override fun onEnable() {
		plugin = this

		Metrics(this, 12863)

		saveDefaultConfig()
		reloadConfig()

		Bukkit.getPluginManager().registerEvents(InterfaceListener(), this)
		Bukkit.getPluginManager().registerEvents(CustomBlocksListener(), this)

		plugin.getCommand("msp")!!.setExecutor(Commands())
		plugin.getCommand("msp")!!.tabCompleter = CommandTabComplete()
	}

	override fun reloadConfig() {
		super.reloadConfig()

		timeOperations = plugin.config.getBoolean("timeOperations", false)
		detectionLimit = plugin.config.getInt("detectionLimit", 500000)

		val newForcedUndetectable = mutableSetOf<MSPMaterial>()
		plugin.config.getStringList("forcedUndetectable").forEach {
			newForcedUndetectable.add(MSPMaterial(it))
		}
		forcedUndetectable = newForcedUndetectable

		val newDefaultUndetectable = mutableSetOf<MSPMaterial>()
		plugin.config.getStringList("defaultUndetectable").forEach {
			newDefaultUndetectable.add(MSPMaterial(it))
		}
		defaultUndetectable = newDefaultUndetectable
	}
}