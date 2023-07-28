package net.stellarica.server.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Subcommand
import net.minecraft.core.BlockPos
import net.stellarica.server.material.custom.item.isPowerable
import net.stellarica.server.material.custom.item.power
import net.stellarica.server.material.type.block.BlockType
import net.stellarica.server.material.type.item.CustomItemType
import net.stellarica.server.material.type.item.ItemType
import net.stellarica.server.multiblock.MultiblockHandler
import net.stellarica.server.util.extension.craft
import net.stellarica.server.util.extension.toBlockPosition
import net.stellarica.server.util.extension.toLocation
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("Unused")
@CommandAlias("debug|db")
@CommandPermission("stellarica.debug")
class DebugCommands : BaseCommand() {

	@Subcommand("multiblock|mb dump")
	@Description("Dump loaded multiblocks")
	fun onMultiblockDump(sender: CommandSender) {
		sender.sendRichMessage(MultiblockHandler.multiblocks.toString())
	}

	@Subcommand("material|mat get")
	@Description("Check whether the held item is a custom item")
	fun onMaterialGetItem(sender: Player) {
		val item = sender.inventory.itemInMainHand
		val custom = (ItemType.of(item) as? CustomItemType)?.item ?: run {
			sender.sendRichMessage("<gold>This item is not a custom item!")
			return
		}
		sender.sendRichMessage(
				"""
			<white>ID:<gray> ${custom.id}
			<white>Display Name:<gray> ${custom.name}<reset>
			<white>Custom Model Data:<gray> ${custom.modelData}
			<white>Base Material:<gray> ${custom.base}
			""".trimIndent() +
						if (item.isPowerable) {
							"\n<white>Power: <gray>${item.power}/${custom.maxPower}\n "
						} else ""
		)
	}

	@Subcommand("material|mat setpower|sp")
	@Description("Set the power of the held item")
	fun onMaterialSetPower(sender: Player, power: Int) {
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


	@Subcommand("material|mat block")
	@Description("Get the block type of the block you're looking at")
	fun onMaterialGetBlock(sender: Player) {
		val block = sender.getTargetBlockExact(10)
		sender.sendRichMessage("<green>Block: ${BlockType.of(block!!)}")
	}

}