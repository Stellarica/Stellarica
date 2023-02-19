package net.stellarica.server

import co.aikar.commands.PaperCommandManager
import mu.KotlinLogging
import net.minecraft.resources.ResourceLocation
import net.stellarica.server.crafts.pilotables.ControlQueueRunnable
import net.stellarica.server.crafts.pilotables.starships.Starship
import net.stellarica.server.crafts.pilotables.starships.commands.StarshipCommands
import net.stellarica.server.crafts.pilotables.starships.commands.StarshipDebugCommands
import net.stellarica.server.crafts.pilotables.starships.listeners.InterfaceListener
import net.stellarica.server.crafts.pilotables.starships.subsystems.armor.ArmorValues
import net.stellarica.server.customblocks.CustomBlockListener
import net.stellarica.server.customblocks.MushroomEventListener
import net.stellarica.server.customitems.CustomItems
import net.stellarica.server.customitems.commands.CustomItemCommands
import net.stellarica.server.customitems.listeners.ItemEnchantListener
import net.stellarica.server.customitems.listeners.PowerItemBreakListener
import net.stellarica.server.networking.BukkitNetworkHandler
import net.stellarica.server.networking.Handshake
import net.stellarica.server.utils.extensions.TestDebugCommand
import org.bukkit.Bukkit.getPluginManager
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

/**
 * Base plugin class for Stellarica
 */
class StellaricaServer : JavaPlugin() {
	companion object {
		/**
		 * The plugin instance. Not best practice to have it static,
		 * but the convenience is worth it.
		 *
		 * Sorry :)
		 */
		lateinit var plugin: StellaricaServer
			private set

		/**
		 * The currently piloted [Starship]s
		 */
		var pilotedCrafts = mutableSetOf<Starship>()

		/**
		 * kotlin-logging logger for Stellarica
		 * @see getLogger
		 */
		val klogger = KotlinLogging.logger("Stellarica")

		fun identifier(path: String) = ResourceLocation("stellarica", path)
	}

	@Deprecated("Use kotlin-logging instead", ReplaceWith("klogger"))
	override fun getLogger(): Logger = super.getLogger()

	lateinit var networkHandler: BukkitNetworkHandler

	override fun onEnable() {
		// Plugin init
		plugin = this

		networkHandler = BukkitNetworkHandler()

		// Register listeners here
		arrayOf(
			MushroomEventListener(),
			CustomBlockListener(),
			InterfaceListener(),
			PowerItemBreakListener(),
			ItemEnchantListener(),
			ArmorValues,
			Handshake()
		).forEach { getPluginManager().registerEvents(it, this) }

		// Register commands here
		val commandManager = PaperCommandManager(this)
		arrayOf(
			StarshipCommands(),
			StarshipDebugCommands(),
			CustomItemCommands(),
			TestDebugCommand(),
		).forEach { commandManager.registerCommand(it) }
		commandManager.commandCompletions.registerCompletion(
			"customitems"
		) { CustomItems.all.keys }

		// Start the bukkit tasks
		ControlQueueRunnable.runTaskTimer(plugin, 1, 1)
	}
}
