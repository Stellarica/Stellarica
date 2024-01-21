package net.stellarica.server

import cloud.commandframework.annotations.AnnotationParser
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator
import cloud.commandframework.meta.SimpleCommandMeta
import cloud.commandframework.paper.PaperCommandManager
import mu.KotlinLogging
import net.minecraft.resources.ResourceLocation
import net.stellarica.server.command.CustomItemCommand
import net.stellarica.server.command.MultiblockCommand
import net.stellarica.server.command.Temporary
import net.stellarica.server.material.block.CustomBlockHandler
import net.stellarica.server.material.item.CustomItemHandler
import net.stellarica.server.multiblock.MultiblockHandler
import net.stellarica.server.networking.BukkitNetworkHandler
import net.stellarica.server.networking.ModdedPlayerHandler
import net.stellarica.server.script.Scripting
import org.bukkit.Bukkit.getPluginManager
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

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

	var scriptingEnabled = false
		private set

	override fun onEnable() {
		val start = System.currentTimeMillis()
		plugin = this

		checkNebula()
		checkGraalVM()

		networkHandler = BukkitNetworkHandler()

		val commandManager = PaperCommandManager(
			this,
			AsynchronousCommandExecutionCoordinator.builder<CommandSender>().build(),
			{ it },
			{ it }
		).also {
			it.registerAsynchronousCompletions()
		}

		val parser = AnnotationParser(
			commandManager, CommandSender::class.java
		) { SimpleCommandMeta.empty() }

		arrayOf(
			Temporary,
			CustomItemCommand,
			MultiblockCommand
		).forEach { parser.parse(it) }

		// Register listeners here
		arrayOf(
			CustomItemHandler,
			CustomBlockHandler,
			ModdedPlayerHandler,
			MultiblockHandler
		).forEach { getPluginManager().registerEvents(it, this) }

		klogger.info { "Plugin startup took ${System.currentTimeMillis() - start}ms" }
	}


	private fun checkNebula() {
		if (this.server.name != "Nebula") klogger.error {
			"""
			Stellarica requires the Nebula server software, but seems to be running on ${this.server.name}!
			You can find Nebula at https://github.com/Stellarica/Nebula
				
			The plugin will attempt to load anyway, but many features will be broken!
			""".trimIndent()
		} else {
			klogger.info { "Nebula in use!" }
		}
	}

	private fun checkGraalVM() {
		val vendor = System.getProperty("java.vendor.version")
		klogger.info { "Running on $vendor" }
		if (!vendor.contains("Graal")) {
			klogger.warn {
				"""
				Stellarica requires GraalVM for in-game scripting, but seems to be running on ${System.getProperty("java.vm.name")}!
				You can find GraalVM at https://www.graalvm.org/
				
				Most things should still work, but scripting will be disabled!
				""".trimIndent()
			}
			scriptingEnabled = false
		} else {
			scriptingEnabled = true
			Scripting.setup()
		}
	}
}
