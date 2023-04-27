package net.stellarica.server.material.custom

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
import net.stellarica.server.material.custom.item.isPowerable
import net.stellarica.server.material.custom.item.power
import net.stellarica.server.material.type.block.BlockType
import net.stellarica.server.material.type.item.CustomItemType
import net.stellarica.server.material.type.item.ItemType
import org.bukkit.entity.Player

@Suppress("Unused")
@CommandAlias("customitem|citem|ci")
class CustomMaterialCommands : BaseCommand() {
	@Subcommand("give|g")
	@Description("Get a custom item")
	@CommandPermission("stellarica.material.give.self")
	@CommandCompletion("@customitems")
	fun onGive(
		sender: Player,
		id: String,
		@Default("1") count: Int,
		@Optional target: Player = sender
	) {
		val item: CustomItem = (ItemType.of(StellaricaServer.identifier(id)) as? CustomItemType)?.item ?: run {
			sender.sendRichMessage("<red>No custom item with the id '$id' found.")
			return
		}
		if (target != sender && !target.hasPermission("stellarica.material.give.other")) {
			sender.sendRichMessage("<red>You do not have permission to give custom items to other players.")
			return
		}
		target.inventory.addItem(ItemType.of(item).getBukkitItemStack(count))
		sender.sendRichMessage("Gave <b>$count</b> of ${item.name}<reset> to ${target.name}.")
	}
}

