package net.stellarica.server.craft.starship

import cloud.commandframework.annotations.CommandMethod
import org.bukkit.entity.Player

object Temporary {
    @CommandMethod("ship")
    fun pilot(sender: Player) {
        sender.sendRichMessage("<blue>Catstare")
    }
}