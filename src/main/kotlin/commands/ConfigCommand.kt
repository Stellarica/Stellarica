package io.github.petercrawley.minecraftstarshipplugin.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Subcommand
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin
import org.bukkit.command.CommandSender
import java.io.File

@CommandAlias("hydrazine config|hc")
class ConfigCommand: BaseCommand() {
	@Subcommand("reload")
	@Description("Reloads the Hydrazine config files")
	@CommandPermission("hydrazine.config.reload")
	fun onConfigReload(sender: CommandSender){
		MinecraftStarshipPlugin.plugin.saveDefaultConfig()
		MinecraftStarshipPlugin.plugin.reloadConfig()
		sender.sendMessage("Reloaded config")
	}

	@Subcommand("reset")
	@Description("Resets the Hydrazine config files")
	@CommandPermission("hydrazine.config.reset")
	fun onConfigReset(sender: CommandSender) {
		val file = File(MinecraftStarshipPlugin.plugin.dataFolder, "config.yml")
		file.delete()
		MinecraftStarshipPlugin.plugin.saveDefaultConfig()
		MinecraftStarshipPlugin.plugin.reloadConfig()
		sender.sendMessage("Reset config")
	}
}