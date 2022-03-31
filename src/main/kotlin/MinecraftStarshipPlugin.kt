package io.github.petercrawley.minecraftstarshipplugin

import io.github.petercrawley.minecraftstarshipplugin.commands.CommandTabComplete
import io.github.petercrawley.minecraftstarshipplugin.commands.Commands
import io.github.petercrawley.minecraftstarshipplugin.customMaterials.CustomBlocksListener
import io.github.petercrawley.minecraftstarshipplugin.customMaterials.MSPMaterial
import io.github.petercrawley.minecraftstarshipplugin.events.MSPConfigReloadEvent
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit.getPluginManager
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

// 		Metrics(this, 12863)

		getPluginManager().registerEvents(CustomBlocksListener(), this)

		//   /-\
		//  / ! \  MUST BE CALLED AFTER REGISTERING EVENTS!
		// /_____\
		saveDefaultConfig()
		reloadConfig()

		plugin.getCommand("msp")!!.setExecutor(Commands())
		plugin.getCommand("msp")!!.tabCompleter = CommandTabComplete()
	}

	override fun reloadConfig() {
		super.reloadConfig()

		getPluginManager().callEvent(MSPConfigReloadEvent())

//		timeOperations = config.getBoolean("timeOperations", false)
//		detectionLimit = config.getInt("detectionLimit", 500000)
//
//		val newForcedUndetectable = mutableSetOf<MSPMaterial>()
//		config.getStringList("forcedUndetectable").forEach {
//			newForcedUndetectable.add(MSPMaterial(it))
//		}
//		forcedUndetectable = newForcedUndetectable
//
//		val newDefaultUndetectable = mutableSetOf<MSPMaterial>()
//		config.getStringList("defaultUndetectable").forEach {
//			newDefaultUndetectable.add(MSPMaterial(it))
//		}
//		defaultUndetectable = newDefaultUndetectable
	}
}
