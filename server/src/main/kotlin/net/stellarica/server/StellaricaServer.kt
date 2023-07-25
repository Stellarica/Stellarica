package net.stellarica.server

import co.aikar.commands.PaperCommandManager
import mu.KotlinLogging
import net.minecraft.resources.ResourceLocation
import net.stellarica.server.command.CustomMaterialCommands
import net.stellarica.server.material.custom.block.CustomBlockHandler
import net.stellarica.server.material.custom.block.CustomBlocks
import net.stellarica.server.material.custom.item.CustomItemHandler
import net.stellarica.server.material.custom.item.CustomItems
import net.stellarica.server.networking.BukkitNetworkHandler
import net.stellarica.server.networking.ModdedPlayerHandler
import net.stellarica.server.command.DebugCommands
import org.bukkit.Bukkit.getPluginManager
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
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
		 * kotlin-logging logger for Stellarica
		 * @see getLogger
		 */
		val klogger = KotlinLogging.logger("Stellarica")

		fun identifier(path: String) = ResourceLocation("stellarica", path)
		fun namespacedKey(path: String) = NamespacedKey(plugin, path)
	}

	val moddedPlayers = mutableSetOf<Player>()

	@Deprecated("Use kotlin-logging instead", ReplaceWith("klogger"))
	override fun getLogger(): Logger = super.getLogger()

	lateinit var networkHandler: BukkitNetworkHandler

	override fun onEnable() {
		if (this.server.name != "Nebula") klogger.error {
			"""
			Stellarica requires the Nebula server software, but seems to be running on ${this.server.name}!
			You can find Nebula at https://github.com/Stellarica/Nebula
				
			The plugin will attempt to load anyway, but many features will be broken!
			""".trimIndent()
		}

		plugin = this

		networkHandler = BukkitNetworkHandler()

		// Register listeners here
		arrayOf(
			CustomItemHandler,
			CustomBlockHandler,
			ModdedPlayerHandler,
		).forEach { getPluginManager().registerEvents(it, this) }

		// Register commands here
		val commandManager = PaperCommandManager(this)
		arrayOf(
				CustomMaterialCommands(),
				DebugCommands()
		).forEach { commandManager.registerCommand(it) }

		commandManager.commandCompletions.registerCompletion(
				"customitems"
		) { CustomItems.all.map { it.id.path } }
		commandManager.commandCompletions.registerCompletion(
				"customblocks"
		) { CustomBlocks.all.map { it.id.path } }
	}
}
