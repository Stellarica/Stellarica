package io.github.petercrawley.minecraftstarshipplugin

import io.github.petercrawley.minecraftstarshipplugin.commands.CommandTabComplete
import io.github.petercrawley.minecraftstarshipplugin.commands.Commands
import io.github.petercrawley.minecraftstarshipplugin.customMaterials.CustomBlocksListener
import io.github.petercrawley.minecraftstarshipplugin.customMaterials.MSPMaterial
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit.getPluginManager
import org.bukkit.plugin.java.JavaPlugin

class MinecraftStarshipPlugin : JavaPlugin() {
	companion object {
		lateinit var plugin: MinecraftStarshipPlugin
			private set

//		var timeOperations: Boolean = false
//			private set
//
//		var detectionLimit: Int = 500000
//			private set

		var customBlocks = mapOf<Byte, String>()
			private set

//		var forcedUndetectable = setOf<MSPMaterial>()
//			private set
//
//		var defaultUndetectable = setOf<MSPMaterial>()
//			private set
	}

	override fun onEnable() {
		plugin = this

		Metrics(this, 12863)

		saveDefaultConfig()
		reloadConfig()

		getPluginManager().registerEvents(CustomBlocksListener(), this)

		plugin.getCommand("msp")!!.setExecutor(Commands())
		plugin.getCommand("msp")!!.tabCompleter = CommandTabComplete()
	}

	override fun reloadConfig() {
		super.reloadConfig()

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

		val newCustomBlocks = mutableMapOf<Byte, String>()
		config.getStringList("customBlocks").forEach {
			var id = 0

			id += if (config.getBoolean("customBlocks.$it.north")) 32 else 0
			id += if (config.getBoolean("customBlocks.$it.east"))  16 else 0
			id += if (config.getBoolean("customBlocks.$it.south"))  8 else 0
			id += if (config.getBoolean("customBlocks.$it.west"))   4 else 0
			id += if (config.getBoolean("customBlocks.$it.up"))     2 else 0
			id += if (config.getBoolean("customBlocks.$it.down"))   1 else 0

			newCustomBlocks[id.toByte()] = it.uppercase()
		}

		customBlocks = newCustomBlocks
	}
}