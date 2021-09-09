package io.github.petercrawley.minecraftstarshipplugin.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class CommandTabComplete : TabCompleter {
    override fun onTabComplete(sender: CommandSender, cmd: Command, alias: String, args: Array<String>): MutableList<String> {
        val subcommands: MutableList<String> = ArrayList()
        if (args.size == 1) {
            // /msp <x>
            if (sender.hasPermission("msp.config.reload") || sender.hasPermission("msp.config.reset")) {
                subcommands.add("config")
            }
        }

        if (args.size == 2) {
            if (args[0].equals("config", ignoreCase = true)) {
                // /msp config <x>
                if (sender.hasPermission("msp.config.reload")) {
                    subcommands.add("reload")
                }
                if (sender.hasPermission("msp.config.reset")){
                    subcommands.add("reset")
                }
            }
        }
        return subcommands;
    }
}