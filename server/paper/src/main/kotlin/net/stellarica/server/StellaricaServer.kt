package net.stellarica.server

import co.aikar.commands.PaperCommandManager
import mu.KotlinLogging
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Blocks
import net.stellarica.common.utils.OriginRelative
import net.stellarica.server.crafts.starships.Starship
import net.stellarica.server.crafts.starships.commands.StarshipCommands
import net.stellarica.server.crafts.starships.commands.StarshipDebugCommands
import net.stellarica.server.crafts.starships.listeners.InterfaceListener
import net.stellarica.server.customblocks.CustomBlockListener
import net.stellarica.server.customblocks.MushroomEventListener
import net.stellarica.server.material.custom.item.CustomItems
import net.stellarica.server.material.custom.item.commands.CustomItemCommands
import net.stellarica.server.material.custom.item.listeners.ItemEnchantListener
import net.stellarica.server.material.custom.item.listeners.PowerItemBreakListener
import net.stellarica.server.material.block.BlockType
import net.stellarica.server.multiblocks.MultiblockCommands
import net.stellarica.server.multiblocks.MultiblockHandler
import net.stellarica.server.multiblocks.MultiblockType
import net.stellarica.server.networking.BukkitNetworkHandler
import net.stellarica.server.networking.Handshake
import net.stellarica.server.utils.extensions.TestDebugCommand
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
		 * The currently piloted [Starship]s
		 */
		var pilotedCrafts = mutableSetOf<Starship>()

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
		// Plugin init
		plugin = this

		val i = OriginRelative(-1, 0, 2)
		println("$i north ${i.getBlockPos(BlockPos.ZERO, Direction.NORTH)}")
		println("$i south ${i.getBlockPos(BlockPos.ZERO, Direction.SOUTH)}")
		println("$i east ${i.getBlockPos(BlockPos.ZERO, Direction.EAST)}")
		println("$i west ${i.getBlockPos(BlockPos.ZERO, Direction.WEST)}")


		networkHandler = BukkitNetworkHandler()

		// Register listeners here
		arrayOf(
			MushroomEventListener(),
			CustomBlockListener(),
			InterfaceListener(),
			PowerItemBreakListener(),
			ItemEnchantListener(),
			MultiblockHandler,
			Handshake()
		).forEach { getPluginManager().registerEvents(it, this) }

		// Register commands here
		val commandManager = PaperCommandManager(this)
		arrayOf(
			StarshipCommands(),
			StarshipDebugCommands(),
			CustomItemCommands(),
			TestDebugCommand(),
			MultiblockCommands()
		).forEach { commandManager.registerCommand(it) }
		commandManager.commandCompletions.registerCompletion(
			"customitems"
		) { CustomItems.all.keys }

		// this is just for testing
		MultiblockHandler.types.add(
			MultiblockType(
				identifier("test_weapon"),
				mapOf(
					OriginRelative(0, 0, 0) to BlockType.of(Blocks.IRON_BLOCK),
					OriginRelative(1, 0, 0) to BlockType.of(Blocks.IRON_BLOCK),
					OriginRelative(2, 0, 0) to BlockType.of(Blocks.FURNACE),
				)
			)
		)
		MultiblockHandler.types.add(
			MultiblockType(
				identifier("test_shield"),
				mapOf(
					OriginRelative(0, 0, 0) to BlockType.of(Blocks.DIAMOND_BLOCK),
					OriginRelative(1, 0, 0) to BlockType.of(Blocks.GLASS),
					OriginRelative(-1, 0, 0) to BlockType.of(Blocks.GLASS),
					OriginRelative(0, 0, 1) to BlockType.of(Blocks.GLASS),
					OriginRelative(0, 0, -1) to BlockType.of(Blocks.GLASS),
				)
			)
		)
	}
}
