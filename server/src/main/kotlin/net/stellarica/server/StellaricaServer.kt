package net.stellarica.server

import mu.KotlinLogging
import net.minecraft.resources.ResourceLocation
import net.stellarica.server.craft.starship.InterfaceListener
import net.stellarica.server.event.BukkitPriority
import net.stellarica.server.event.listen
import net.stellarica.server.material.custom.block.CustomBlockHandler
import net.stellarica.server.material.custom.feature.jetpack.JetpackListener
import net.stellarica.server.material.custom.item.CustomItemHandler
import net.stellarica.server.multiblock.MultiblockHandler
import net.stellarica.server.networking.BukkitNetworkHandler
import net.stellarica.server.networking.ModdedPlayerHandler
import org.bukkit.Bukkit.getPluginManager
import org.bukkit.event.player.PlayerInteractEvent
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
	}

	@Deprecated("Use kotlin-logging instead", ReplaceWith("klogger"))
	override fun getLogger(): Logger = super.getLogger()

	private lateinit var networkHandler: BukkitNetworkHandler

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
			InterfaceListener(),
			MultiblockHandler,
			CustomItemHandler,
			CustomBlockHandler,
			ModdedPlayerHandler,
			JetpackListener
		).forEach { getPluginManager().registerEvents(it, this) }

		listen<PlayerInteractEvent>({ event ->
			println(event.player.displayName + ": " + event.action + " ")
			println(event.handlers.registeredListeners.first { it == this }.priority)
		}, BukkitPriority.HIGHEST)

	}
}
