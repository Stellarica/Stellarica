package net.stellarica.server.command

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import net.stellarica.server.CustomItems
import net.stellarica.server.StellaricaServer.Companion.identifier
import net.stellarica.server.material.item.type.ItemType
import org.bukkit.entity.Player

@Suppress("unused")
object CustomItemCommand {
	@CommandMethod("customitem get <item>")
	fun get(sender: Player, @Argument("item") item: String) {
		// todo: this needs to go, its just the bare minimum for testing custom items
		val item = CustomItems[identifier(item)] ?: run {
			sender.sendRichMessage("<red>Unknown custom item!")
			return
		}
		sender.inventory.addItem(ItemType.of(item).getBukkitItemStack(1))
	}
}
