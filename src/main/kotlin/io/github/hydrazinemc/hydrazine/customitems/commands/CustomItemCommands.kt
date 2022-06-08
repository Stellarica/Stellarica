package io.github.hydrazinemc.hydrazine.customitems.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Subcommand
import io.github.hydrazinemc.hydrazine.customitems.CustomItems
import io.github.hydrazinemc.hydrazine.customitems.customItem
import io.github.hydrazinemc.hydrazine.utils.extensions.sendMiniMessage
import org.bukkit.entity.Player

/**
 * Command handling for the custom item related commands.
 */
@CommandAlias("customitem|item")
class CustomItemCommands : BaseCommand() {

	/**
	 * Give a player a custom item
	 */
	@Subcommand("give")
	@Description("Get a custom item")
	@CommandPermission("hydrazine.customitems.give")
	fun onGive(sender: Player, id: String, count: Int = 1, target: Player = sender) {
		val item = CustomItems[id] ?: run {
			sender.sendMiniMessage("<red>No custom item with the id '$id' found.")
			return
		}
		target.inventory.addItem(item.getItem(count))
		sender.sendMiniMessage("Gave <b>$count</b> of ${item.name} to ${target.name}.")
	}

	/**
	 * Get whether the held item is a custom item
	 */
	@Subcommand("get")
	@Description("Check whether the held item is a custom item")
	@CommandPermission("hydrazine.customitems.get")
	fun onGet(sender: Player) {
		val custom = sender.inventory.itemInMainHand.customItem ?: run {
			sender.sendMiniMessage("<gold>This item is not a custom item!")
			return
		}
		sender.sendMiniMessage(
			"""
			<green>---- Custom Item ----
			</green>
			ID: ${custom.id}
			Display Name: ${custom.name}<reset>
			Custom Model Data: ${custom.modelData}
			Base Material: ${custom.base}
			""".trimIndent()
		)
	}
}

