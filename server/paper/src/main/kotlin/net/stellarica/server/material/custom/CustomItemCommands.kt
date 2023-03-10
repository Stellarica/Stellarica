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
import net.stellarica.server.material.type.item.CustomItemType
import net.stellarica.server.material.type.item.ItemType
import org.bukkit.entity.Player

/**
 * Command handling for the custom item related commands.
 */
@Suppress("Unused")
@CommandAlias("customitem|item")
class CustomItemCommands : BaseCommand() {

	/**
	 * Give a player a custom item
	 */
	@Subcommand("give")
	@Description("Get a custom item")
	@CommandPermission("stellarica.customitems.give.self")
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
		if (target != sender && !target.hasPermission("stellarica.customitems.give.other")) {
			sender.sendRichMessage("<red>You do not have permission to give custom items to other players.")
			return
		}
		target.inventory.addItem(ItemType.of(item).getBukkitItemStack(count))
		sender.sendRichMessage("Gave <b>$count</b> of ${item.name}<reset> to ${target.name}.")
	}

	/**
	 * Get whether the held item is a custom item
	 */
	@Subcommand("get")
	@Description("Check whether the held item is a custom item")
	@CommandPermission("stellarica.customitems.get")
	fun onGet(sender: Player) {
		val item = sender.inventory.itemInMainHand
		val custom = (ItemType.of(item) as? CustomItemType)?.item ?: run {
			sender.sendRichMessage("<gold>This item is not a custom item!")
			return
		}
		sender.sendRichMessage(
			"""
			<green>---- Custom Item ----
			</green>
			ID: ${custom.id}
			Display Name: ${custom.name}<reset>
			Custom Model Data: ${custom.modelData}
			Base Material: ${custom.base}
			${
				if (item.isPowerable) {
					"Power: ${item.power}/${custom.maxPower}\n "
				} else {
					" "
				}
			}
			""".trimIndent()
		)
	}

	/**
	 * Set the power of the held custom item
	 */
	@Subcommand("setpower")
	@Description("Set the power of the held item")
	@CommandPermission("stellarica.customitems.setpower")
	fun onSetPower(sender: Player, power: Int) {
		val item = sender.inventory.itemInMainHand
		val custom = ItemType.of(item) as? CustomItemType ?: run {
			sender.sendRichMessage("<gold>This item is not a custom item!")
			return
		}
		if (!item.isPowerable) {
			sender.sendRichMessage("<gold>This item is not powerable!")
			return
		}
		item.power = power
		sender.sendRichMessage("<green>Set power to ${item.power}/${custom.item.maxPower}")
	}
}

