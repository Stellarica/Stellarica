package io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Subcommand
import io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.Starship
import io.github.hydrazinemc.hydrazine.utils.Vector3
import io.github.hydrazinemc.hydrazine.utils.extensions.craft
import io.github.hydrazinemc.hydrazine.utils.extensions.sendMiniMessage
import org.bukkit.entity.Player

/**
 * Command handling for the starship debug related commands.
 */
@CommandAlias("shipdebug")
class StarshipDebugCommands : BaseCommand() {

	/**
	 * Set starship velocity
	 */
	@Subcommand("setvelocity")
	@Description("Set the starship velocity")
	@CommandPermission("hydrazine.starship.debug.setvelocity")
	fun onSetVelocity(sender: Player, x: Double, y: Double, z: Double) {
		(getShip(sender)?: return).velocity = Vector3(x, y, z)
		sender.sendMiniMessage("<green>Set starship velocity to ($x, $y, $z)")
	}


	/**
	 * Set starship velocity
	 */
	@Subcommand("setacceleration")
	@Description("Set the starship acceleration")
	@CommandPermission("hydrazine.starship.debug.setacceleration")
	fun onSetAcceleration(sender: Player, x: Double, y: Double, z: Double)  {
		(getShip(sender)?: return).acceleration = Vector3(x, y, z)
		sender.sendMiniMessage("<green>Set starship acceleration to ($x, $y, $z)")
	}

	private fun getShip(sender: Player): Starship? {
		return ((sender.craft ?: run {
			sender.sendMiniMessage("<red>You are not riding a craft!")
			return null
		}) as? Starship) ?: run {
			sender.sendMiniMessage("<gold>This craft is not a Starship!")
			return null
		}
	}
}

