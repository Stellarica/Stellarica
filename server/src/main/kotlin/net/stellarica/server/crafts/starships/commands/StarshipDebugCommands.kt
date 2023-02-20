package net.stellarica.server.crafts.starships.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Subcommand
import net.minecraft.core.BlockPos
import net.stellarica.server.crafts.starships.Starship
import net.stellarica.server.utils.extensions.craft
import net.stellarica.server.utils.extensions.toBlockPos
import net.stellarica.server.utils.extensions.toLocation
import net.stellarica.server.utils.extensions.toVec3
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Player

/**
 * Command handling for the starship debug related commands.
 */
@Suppress("Unused")
@CommandAlias("shipdebug")
class StarshipDebugCommands : BaseCommand() {
	/**
	 * Display the hitbox of the craft
	 */
	@Subcommand("hitbox")
	@Description("View the ship's hitbox")
	@CommandPermission("stellarica.starship.debug.hitbox")
	private fun onShowHitbox(sender: Player) {
		val ship = sender.craft ?: run {
			sender.sendRichMessage("<red>You are not piloting a starship!")
			return
		}
		// this is really terrible, but this is a debug command anyway
		for (x in (sender.location.x.toInt() - 200)..(sender.location.x.toInt() + 200)) {
			for (y in sender.world.minHeight..sender.world.maxHeight) {
				for (z in (sender.location.z.toInt() - 200)..(sender.location.z.toInt() + 200)) {
					val loc = BlockPos(x, y, z)
					if (ship.contains(loc)) {
						sender.world.spawnParticle(
							Particle.BLOCK_MARKER,
							loc.toLocation(sender.world).add(0.5, 0.5, 0.5),
							1,
							0.0,
							0.0,
							0.0,
							0.0,
							Material.BARRIER.createBlockData()
						)
					}
				}
			}
		}
	}

	/**
	 * Display the hitbox of the craft
	 */
	@Subcommand("contains")
	@Description("Whether a block is 'inside' the ship")
	@CommandPermission("stellarica.starship.debug.contains")
	private fun onCheckContains(sender: Player) {
		val ship = sender.craft ?: run {
			sender.sendRichMessage("<red>You are not piloting a starship!")
			return
		}
		sender.sendMessage(ship.contains(sender.getTargetBlockExact(20)?.toBlockPos()).toString())
	}


	private fun getShip(sender: Player): Starship? {
		return ((sender.craft ?: run {
			sender.sendRichMessage("<red>You are not riding a craft!")
			return null
		}) as? Starship) ?: run {
			sender.sendRichMessage("<gold>This craft is not a Starship!")
			return null
		}
	}
}

