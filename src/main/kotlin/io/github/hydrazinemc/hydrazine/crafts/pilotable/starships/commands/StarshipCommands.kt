package io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Subcommand
import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.pilotedCrafts
import io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.Starship
import io.github.hydrazinemc.hydrazine.utils.Tasks
import io.github.hydrazinemc.hydrazine.utils.Vector3
import io.github.hydrazinemc.hydrazine.utils.extensions.craft
import io.github.hydrazinemc.hydrazine.utils.locations.BlockLocation
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player
import java.util.Locale

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
		val ship = getRidingShip(sender) ?: return
		ship.passengers.remove(sender)
		sender.sendRichMessage("<green>Stopped riding!")
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

	private fun getRidingShip(sender: Player): Starship? {
		return (((pilotedCrafts.firstOrNull { it.passengers.contains(sender) } ?: run {
			sender.sendRichMessage("<red>You are not riding a starship!")
			return null
		}) as? Starship) ?: run {
			sender.sendRichMessage("<gold>This craft is not a starship!")
			return null
		}).also {
			if (it.pilot == sender) {
				sender.sendRichMessage("<red>You are piloting this ship!")
				return null
			}
		}
	}
}

