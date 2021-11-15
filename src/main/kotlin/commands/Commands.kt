package io.github.petercrawley.minecraftstarshipplugin.commands

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.plugin
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import java.io.File

class Commands : CommandExecutor {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {

		if (args.isEmpty()){
			sender.sendMessage("Please input at least one argument!")
			return false
		}

		return when (args[0].lowercase()) {

			"config" -> {
				if (args.size == 1){
					sender.sendMessage("Not enough arguments for command!")
					return false
				}
				return when (args[1].lowercase()) {
					"reset" -> if (sender.hasPermission("msp.config.reset")) reset(sender) else false
					"reload" -> if (sender.hasPermission("msp.config.reload")) reload(sender) else false
					else -> {
						sender.sendMessage("Invalid argument(s)!")
						false
					}
				}
			}
			else -> false
		}
	}

	private fun reset(sender: CommandSender): Boolean {
		val file = File(plugin.dataFolder, "config.yml")
		file.delete()

		plugin.saveDefaultConfig()
		plugin.reloadConfig()

		sender.sendMessage("Reset config")
		return true
	}

	private fun reload(sender: CommandSender): Boolean {
		plugin.saveDefaultConfig()
		plugin.reloadConfig()

		sender.sendMessage("Reloaded config")
		return true
	}
}
