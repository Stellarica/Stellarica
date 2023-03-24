package net.stellarica.server.crafts.starships.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Subcommand
import net.stellarica.server.crafts.starships.Starship
import net.stellarica.server.utils.extensions.craft
import net.stellarica.server.utils.extensions.toBlockPos
import net.stellarica.server.utils.extensions.toLocation
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
	@Subcommand("contents")
	@Description("View the ship's contants")
	@CommandPermission("stellarica.starship.debug.contants")
	private fun onShowHitbox(sender: Player) {
		val ship = sender.craft ?: run {
			sender.sendRichMessage("<red>You are not piloting a starship!")
			return
		}

		// todo: make this exist again
		/*
		ship.bounds.forEach {

			val pos = it.getBlockPos(ship.origin, ship.direction)
			sender.world.spawnParticle(
				Particle.BLOCK_MARKER,
				pos.toLocation(sender.world),
				1,
				0.0,
				0.0,
				0.0,
				0.0,
				Material.BARRIER.createBlockData()
			)
		}

		 */
		sender.world.spawnParticle(
			Particle.BLOCK_MARKER,
			ship.origin.toLocation(sender.world),
			1,
			0.0,
			0.0,
			0.0,
			0.0,
			Material.LAPIS_BLOCK.createBlockData()
		)
		ship.multiblocks.forEach{
			val pos = it.getBlockPos(ship.origin, ship.direction)
			sender.world.spawnParticle(
				Particle.BLOCK_MARKER,
				pos.toLocation(sender.world),
				1,
				0.0,
				0.0,
				0.0,
				0.0,
				Material.EMERALD_BLOCK.createBlockData()
			)
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

