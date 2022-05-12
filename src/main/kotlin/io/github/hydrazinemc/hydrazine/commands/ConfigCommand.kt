package io.github.hydrazinemc.hydrazine.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Subcommand
import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.plugin
import org.bukkit.command.CommandSender
import java.io.File

/**
 * Command handling for the configuration file related commands.
 */
@CommandAlias("hconfig|hc")
class ConfigCommand : BaseCommand() {
	@Subcommand("reload")
	@Description("Reloads the Hydrazine config files")
	@CommandPermission("hydrazine.config.reload")
	fun onConfigReload(sender: CommandSender) {
		plugin.saveDefaultConfig()
		plugin.reloadConfig()
		sender.sendMessage("Reloaded config")
	}

	@Subcommand("reset")
	@Description("Resets the Hydrazine config files")
	@CommandPermission("hydrazine.config.reset")
	fun onConfigReset(sender: CommandSender) {
		val file = File(plugin.dataFolder, "config.yml")
		file.delete()
		plugin.saveDefaultConfig()
		plugin.reloadConfig()
		sender.sendMessage("Reset config")
	}
}