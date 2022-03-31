package io.github.hydrazinemc.hydrazine

import co.aikar.commands.PaperCommandManager
import io.github.hydrazinemc.hydrazine.commands.ConfigCommand
import io.github.hydrazinemc.hydrazine.customMaterials.CustomBlocksListener
import io.github.hydrazinemc.hydrazine.events.HydrazineConfigReloadEvent
import org.bukkit.Bukkit.getPluginManager
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin

class Hydrazine : JavaPlugin() {
	companion object {
		lateinit var plugin: Hydrazine
			private set

		var timeOperations: Boolean = false
			private set

		var detectionLimit: Int = 500000
			private set
		var forcedUndetectable = setOf<Material>()
			private set

		var defaultUndetectable = setOf<Material>()
			private set
	}

	override fun onEnable() {
		plugin = this

		getPluginManager().registerEvents(CustomBlocksListener(), this)

		//   /-\
		//  / ! \  MUST BE CALLED AFTER REGISTERING EVENTS!
		// /_____\
		saveDefaultConfig()
		reloadConfig()

		val commandManager = PaperCommandManager(this)
		commandManager.registerCommand(ConfigCommand())
	}

	override fun reloadConfig() {
		super.reloadConfig()

		getPluginManager().callEvent(HydrazineConfigReloadEvent())

		timeOperations = config.getBoolean("timeOperations", false)
		detectionLimit = config.getInt("detectionLimit", 500000)
		val newForcedUndetectable = mutableSetOf<Material>()
		config.getStringList("forcedUndetectable").forEach {
			newForcedUndetectable.add(Material.getMaterial(it)!!)
		}
		forcedUndetectable = newForcedUndetectable

		val newDefaultUndetectable = mutableSetOf<Material>()
		config.getStringList("defaultUndetectable").forEach {
			newDefaultUndetectable.add(Material.getMaterial(it)!!)
		}
		defaultUndetectable = newDefaultUndetectable
	}
}
