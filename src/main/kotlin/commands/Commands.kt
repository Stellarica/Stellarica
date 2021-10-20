package io.github.petercrawley.minecraftstarshipplugin.commands

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.plugin
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import java.io.File

class Commands : CommandExecutor {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
		if (args.isEmpty()) {
			sender.sendMessage("Please input at least one argument!")
			return false
		}
		if (args[0].lowercase() == "config") {
			if (args.size == 1) {
				sender.sendMessage("Not enough arguments for command!")
				return false
			}
			return when (args[1].lowercase()) {
				"reset" -> if (sender.hasPermission("msp.config.reset")) resetConfig(sender, args.getOrNull(2)) else false
				"reload" -> if (sender.hasPermission("msp.config.reload")) reloadConfig(sender) else false
				else -> {
					sender.sendMessage("Invalid argument(s)!")
					false
				}
			}
		}
		return false
	}

	private fun resetConfig(sender: CommandSender, config: String?): Boolean {
		if (config == null){
			sender.sendMessage("Please specify the config file to reset!")
			return true
		}

		val file = File(plugin.dataFolder, config)
		if (!file.exists()){
			sender.sendMessage("That config file does not exist!")
			return true
		}
		file.delete()

		plugin.reloadConfig()
		sender.sendMessage("Reset config $config")
		return true
	}

	private fun reloadConfig(sender: CommandSender): Boolean {
		plugin.reloadConfig()
		sender.sendMessage("Reloaded config")
		return true
	}
}
