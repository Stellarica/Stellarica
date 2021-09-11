package io.github.petercrawley.minecraftstarshipplugin.commands

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.hjson.JsonValue
import java.io.File

class Commands : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isEmpty()){
            sender.sendMessage("Please input at least one argument!")
            return false;
        }
        if (args[0].equals("config",ignoreCase = true) &&
            (sender.hasPermission("msp.config.reset") || sender.hasPermission("msp.config.reload"))) {

            if (args.size == 1) {
                sender.sendMessage("Not enough arguments for command!")
                return false;
            }
            if (args[1].equals("reset", ignoreCase = true)) {
                return resetConfig(sender)
            }
            if (args[1].equals("reload", ignoreCase = true) && sender.hasPermission("msp.config.reload")) {
                return reloadConfig(sender)
            }
        }
        return false
    }

    private fun resetConfig(sender: CommandSender): Boolean{
        val plugin = MinecraftStarshipPlugin.getPlugin()
        File(plugin.dataFolder, "undetectables.hjson").delete()
        File(plugin.dataFolder, "config.hjson").delete()
        plugin.saveDefault("undetectables.hjson")
        plugin.saveDefault("config.hjson")
        plugin.reloadConfig()
        plugin.updateNonDetectableBlocks()
        sender.sendMessage("Reset config")
        MinecraftStarshipPlugin.getPlugin().config = JsonValue.readHjson(File(MinecraftStarshipPlugin.getPlugin().dataFolder, "config.hjson").bufferedReader()).asObject()!!
        return true
    }

    private fun reloadConfig(sender: CommandSender): Boolean{
        MinecraftStarshipPlugin.getPlugin().config = JsonValue.readHjson(File(MinecraftStarshipPlugin.getPlugin().dataFolder, "config.hjson").bufferedReader()).asObject()!!
        MinecraftStarshipPlugin.getPlugin().reloadConfig()
        MinecraftStarshipPlugin.getPlugin().updateNonDetectableBlocks()
        sender.sendMessage("Reloaded config")
        return true
    }
}