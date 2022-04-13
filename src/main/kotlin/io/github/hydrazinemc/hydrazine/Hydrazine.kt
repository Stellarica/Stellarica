package io.github.hydrazinemc.hydrazine

import co.aikar.commands.PaperCommandManager
import io.github.hydrazinemc.hydrazine.commands.ConfigCommand
import io.github.hydrazinemc.hydrazine.customblocks.CustomBlocksListener
import io.github.hydrazinemc.hydrazine.events.HydrazineConfigReloadEvent
import io.github.hydrazinemc.hydrazine.starships.Starship
import io.github.hydrazinemc.hydrazine.starships.StarshipBlockSetter
import io.github.hydrazinemc.hydrazine.starships.listeners.InterfaceListener
import io.github.hydrazinemc.hydrazine.utils.ConfigurableValues
import org.bukkit.Bukkit.getPluginManager
import org.bukkit.plugin.java.JavaPlugin

class Hydrazine : JavaPlugin() {
	companion object {
		lateinit var plugin: Hydrazine
			private set

		var activeStarships = mutableSetOf<Starship>()
	}

	override fun onEnable() {
		plugin = this

		getPluginManager().registerEvents(CustomBlocksListener(), this)
		getPluginManager().registerEvents(InterfaceListener(), this)

		//   /-\
		//  / ! \  MUST BE CALLED AFTER REGISTERING EVENTS!
		// /_____\
		saveDefaultConfig()
		reloadConfig()

		val commandManager = PaperCommandManager(this)
		commandManager.registerCommand(ConfigCommand())

		StarshipBlockSetter.runTaskTimer(plugin, 0, 1)
	}

	override fun reloadConfig() {
		super.reloadConfig()
		getPluginManager().callEvent(HydrazineConfigReloadEvent())
		ConfigurableValues.loadFromConfig()
	}
}