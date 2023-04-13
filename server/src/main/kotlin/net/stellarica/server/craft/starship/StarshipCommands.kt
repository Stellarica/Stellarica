package net.stellarica.server.craft.starship

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Subcommand
import net.minecraft.world.level.block.Blocks
import net.stellarica.server.StellaricaServer.Companion.pilotedCrafts
import net.stellarica.server.util.extension.craft
import org.bukkit.entity.Player

/**
 * Command handling for the starship related commands.
 */
@Suppress("Unused")
@CommandAlias("ship")
class StarshipCommands : BaseCommand() {

	@Subcommand("unpilot")
	@Description("Unpilot a piloted ship")
	private fun onUnpilot(sender: Player) {
		val ship = getPilotedShip(sender) ?: return
		ship.deactivateCraft()
	}

	@Subcommand("stopriding")
	@Description("Stop riding a ship")
	private fun onStopRiding(sender: Player) {
		val ship = getShip(sender) ?: return
		if (sender == ship.pilot) {
			ship.deactivateCraft()
		} else {
			ship.passengers.remove(sender)
			sender.sendRichMessage("<green>Stopped riding!")
		}
	}

	@Subcommand("nuke")
	@Description("Bye bye ship... :(")
	private fun onNuke(sender: Player) {
		val ship = getPilotedShip(sender) ?: return
		val blocks = ship.detectedBlocks.toSet() // is this neccecary?
		val state = Blocks.AIR.defaultBlockState()

		ship.deactivateCraft()

		for (block in blocks) {
			ship.world.setBlock(block, state, 0)
		}
		sender.sendRichMessage("<red>Ship nuked!")
	}

	private fun getPilotedShip(sender: Player): Starship? {
		return ((sender.craft ?: run {
			sender.sendRichMessage("<red>You are not piloting a starship!!")
			return null
		}) as? Starship) ?: run {
			sender.sendRichMessage("<gold>This craft is not a starship!")
			return null
		}
	}

	private fun getShip(sender: Player): Starship? {
		return ((pilotedCrafts.firstOrNull { it.passengers.contains(sender) } ?: run {
			sender.sendRichMessage("<red>You are not riding a starship!")
			return null
		}) as? Starship) ?: run {
			sender.sendRichMessage("<gold>This craft is not a starship!")
			return null
		}
	}

	private fun getRidingShip(sender: Player): Starship? {
		return getShip(sender).also {
			if (it?.pilot == sender) {
				sender.sendRichMessage("<red>You are piloting this ship!")
				return null
			}
		}
	}
}

