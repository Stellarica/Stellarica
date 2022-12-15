package io.github.hydrazinemc.hydrazine.server

import co.aikar.commands.PaperCommandManager
import io.github.hydrazinemc.hydrazine.server.commands.ConfigCommand
import io.github.hydrazinemc.hydrazine.server.crafts.pilotable.ControlQueueRunnable
import io.github.hydrazinemc.hydrazine.server.crafts.pilotable.Pilotable
import io.github.hydrazinemc.hydrazine.server.crafts.pilotable.starships.commands.StarshipCommands
import io.github.hydrazinemc.hydrazine.server.crafts.pilotable.starships.commands.StarshipDebugCommands
import io.github.hydrazinemc.hydrazine.server.crafts.pilotable.starships.listeners.InterfaceListener
import io.github.hydrazinemc.hydrazine.server.crafts.pilotable.starships.subsystem.armor.ArmorValues
import io.github.hydrazinemc.hydrazine.server.customblocks.CustomBlockListener
import io.github.hydrazinemc.hydrazine.server.customblocks.CustomBlocks
import io.github.hydrazinemc.hydrazine.server.customblocks.MushroomEventListener
import io.github.hydrazinemc.hydrazine.server.customitems.CustomItems
import io.github.hydrazinemc.hydrazine.server.customitems.commands.CustomItemCommands
import io.github.hydrazinemc.hydrazine.server.customitems.listeners.ItemEnchantListener
import io.github.hydrazinemc.hydrazine.server.customitems.listeners.PowerItemBreakListener
import io.github.hydrazinemc.hydrazine.server.events.HydrazineConfigReloadEvent
import io.github.hydrazinemc.hydrazine.server.multiblocks.Multiblocks
import io.github.hydrazinemc.hydrazine.server.multiblocks.commands.MultiblockCommands
import io.github.hydrazinemc.hydrazine.server.utils.ConfigurableValues
import io.github.hydrazinemc.hydrazine.server.utils.extensions.TestDebugCommand
import mu.KotlinLogging
import org.bukkit.Bukkit.getPluginManager
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

/**
 * Base plugin class for Hydrazine
 */
class HydrazineServer : JavaPlugin() {
	companion object {
		/**
		 * The plugin instance. Not best practice to have it static,
		 * but the convenience is worth it.
		 *
		 * Sorry :)
		 */
		lateinit var plugin: HydrazineServer
			private set

		/**
		 * The currently piloted [Pilotable]s
		 */
		var pilotedCrafts = mutableSetOf<Pilotable>()

		/**
		 * kotlin-logging logger for Hydrazine
		 * @see getLogger
		 */
		val klogger = KotlinLogging.logger("Hydrazine")
	}

	@Deprecated("Use kotlin-logging instead", ReplaceWith("klogger"))
	override fun getLogger(): Logger = super.getLogger()


	override fun onEnable() {
		// Plugin init

		plugin = this

		// Register listeners here
		arrayOf(
			MushroomEventListener(),
			CustomBlockListener(),
			InterfaceListener(),
			PowerItemBreakListener(),
			ItemEnchantListener(),
			Multiblocks,
			ArmorValues,
		).forEach { getPluginManager().registerEvents(it, this) }

		// Register commands here
		val commandManager = PaperCommandManager(this)
		arrayOf(
			ConfigCommand(),
			StarshipCommands(),
			StarshipDebugCommands(),
			CustomItemCommands(),
			MultiblockCommands(),
			TestDebugCommand(),
		).forEach { commandManager.registerCommand(it) }
		commandManager.commandCompletions.registerCompletion(
			"customitems"
		) { CustomItems.all.keys }

		// Reload the config
		saveDefaultConfig()
		reloadConfig()

		// Start the bukkit tasks
		ControlQueueRunnable.runTaskTimer(plugin, 1, 1)
	}

	override fun reloadConfig() {
		super.reloadConfig()
		CustomItems.loadFromConfig() // needs to be called before custom blocks
		CustomBlocks.loadFromConfig()

		getPluginManager().callEvent(HydrazineConfigReloadEvent())
		ConfigurableValues.loadFromConfig()
	}
}
