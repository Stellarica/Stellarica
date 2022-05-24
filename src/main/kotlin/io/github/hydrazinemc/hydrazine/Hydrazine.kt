package io.github.hydrazinemc.hydrazine

import co.aikar.commands.PaperCommandManager
import io.github.hydrazinemc.hydrazine.commands.ConfigCommand
import io.github.hydrazinemc.hydrazine.crafts.CraftBlockSetter
import io.github.hydrazinemc.hydrazine.crafts.pilotable.Pilotable
import io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.StarshipMover
import io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.listeners.InterfaceListener
import io.github.hydrazinemc.hydrazine.customblocks.CustomBlocksListener
import io.github.hydrazinemc.hydrazine.events.HydrazineConfigReloadEvent
import io.github.hydrazinemc.hydrazine.utils.ConfigurableValues
import org.bukkit.Bukkit.getPluginManager
import org.bukkit.plugin.java.JavaPlugin

/**
 * Base plugin class
 */
class Hydrazine : JavaPlugin() {
	companion object {
		/**
		 * The plugin instance. Not best practice to have it static,
		 * but the convenience is worth it.
		 */
		lateinit var plugin: Hydrazine
			private set

		/**
		 * The currently piloted [Pilotable]s
		 */
		var pilotedCrafts = mutableSetOf<Pilotable>()
	}

	override fun onEnable() {
		plugin = this

		getPluginManager().registerEvents(CustomBlocksListener(), this)
		getPluginManager().registerEvents(InterfaceListener(), this)

		//   /-\
		//  / ! \  MUST BE CALLED AFTER REGISTERING EVENTS!
		// /_____\
		// ^ idk why, this is a leftover from MSP
		saveDefaultConfig()
		reloadConfig()

		val commandManager = PaperCommandManager(this)
		commandManager.registerCommand(ConfigCommand())

		CraftBlockSetter.runTaskTimer(plugin, 1, 1)
		StarshipMover.runTaskTimer(plugin, 1, 1)
	}

	override fun reloadConfig() {
		super.reloadConfig()
		getPluginManager().callEvent(HydrazineConfigReloadEvent())
		ConfigurableValues.loadFromConfig()
	}
}
