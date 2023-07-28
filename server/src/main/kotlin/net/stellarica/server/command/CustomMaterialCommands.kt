package net.stellarica.server.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Optional
import co.aikar.commands.annotation.Subcommand
import net.stellarica.server.StellaricaServer
import net.stellarica.server.material.custom.item.CustomItem
import net.stellarica.server.material.type.item.CustomItemType
import net.stellarica.server.material.type.item.ItemType
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("Unused")
@CommandAlias("customitem|citem|ci")
class CustomMaterialCommands : BaseCommand() {
	@Subcommand("give|g")
	@Description("Get a custom item")
	@CommandPermission("stellarica.material.give.self")
	@CommandCompletion("@customitems")
	fun onGive(
		sender: CommandSender,
		id: String,
		@Default("1") count: Int,
		@Optional target: Player?
	) {
		val t = target ?: sender as? Player ?: run {
			sender.sendRichMessage("<red>No target specified!")
			return
		}
		val item: CustomItem = (ItemType.of(StellaricaServer.identifier(id)) as? CustomItemType)?.item ?: run {
			sender.sendRichMessage("<red>No custom item with the id '$id' found.")
			return
		}
		if (t != sender && !sender.hasPermission("stellarica.material.give.other")) {
			sender.sendRichMessage("<red>You do not have permission to give custom items to other players.")
			return
		}
		t.inventory.addItem(ItemType.of(item).getBukkitItemStack(count))
		sender.sendRichMessage("Gave <b>$count</b> of ${item.name}<reset> to ${t.name}.")
	}
}

