package io.github.petercrawley.minecraftstarshipplugin.commands

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import java.io.File


class MSPCommands : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args[0].equals("config",ignoreCase = true) && sender.hasPermission("msp.config.reset")){
            if (args[1].equals("reset",ignoreCase = true)){
                return resetConfig(sender)
            }
            if (args[1].equals("reload",ignoreCase = true) && sender.hasPermission("msp.config.reload")){
                return reloadConfig(sender)
            }
        }
        return false
    }


    private fun resetConfig(sender: CommandSender): Boolean{
        val plugin = MinecraftStarshipPlugin.getPlugin()
        val configFile = File(plugin.dataFolder, "config.yml")
        configFile.delete()
        plugin.saveDefaultConfig()
        plugin.reloadConfig()
        plugin.updateNonDetectableBlocks()
        sender.sendMessage("Reset config")
        return true
    }

    private fun reloadConfig(sender: CommandSender): Boolean{
        MinecraftStarshipPlugin.getPlugin().reloadConfig()
        MinecraftStarshipPlugin.getPlugin().updateNonDetectableBlocks()
        sender.sendMessage("Reloaded config")
        return true
    }
}