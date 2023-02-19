package net.stellarica.server.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Subcommand
import net.stellarica.server.StellaricaServer.Companion.plugin
import org.bukkit.command.CommandSender
import java.io.File

/**
 * Command handling for the configuration file related commands.
 */
@CommandAlias("hconfig")
class ConfigCommand : BaseCommand() {

	/**
	 * Reload the configuration file
	 */
	@Subcommand("reload")
	@Description("Reloads the Stellarica config files")
	@CommandPermission("stellarica.config.reload")
	fun onConfigReload(sender: CommandSender) {
		plugin.saveDefaultConfig()
		plugin.reloadConfig()
		sender.sendMessage("Reloaded config")
	}

	/**
	 * Reset the configuration file
	 */
	@Subcommand("reset")
	@Description("Resets the Stellarica config files")
	@CommandPermission("stellarica.config.reset")
	fun onConfigReset(sender: CommandSender) {
		val file = File(plugin.dataFolder, "config.yml")
		file.delete()
		plugin.saveDefaultConfig()
		plugin.reloadConfig()
		sender.sendMessage("Reset config")
	}
}
