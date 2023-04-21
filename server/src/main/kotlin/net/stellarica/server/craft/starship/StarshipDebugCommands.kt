package net.stellarica.server.craft.starship

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Subcommand
import net.stellarica.common.util.OriginRelative
import net.stellarica.server.multiblock.data.ThrusterMultiblockData
import net.stellarica.server.util.extension.craft
import net.stellarica.server.util.extension.toBlockPos
import net.stellarica.server.util.extension.toLocation
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Player

/**
 * Command handling for the starship debug related commands.
 */
@Suppress("Unused")
@CommandAlias("shipdebug")
@CommandPermission("stellarica.debug.starship")
class StarshipDebugCommands : BaseCommand() {

	@Subcommand("contents")
	@Description("View the ship's contants")
	private fun onShowHitbox(sender: Player) {
		val ship = getShip(sender) ?: return
		for ((col, extremes) in ship.contents) {
			for (y in extremes.first..extremes.second) {
				val pos = OriginRelative(col.x, y, col.z).getBlockPos(ship.origin, ship.direction)
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
		}

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
		for (multiblock in ship.multiblocks) {
			val pos = multiblock.getBlockPos(ship.origin, ship.direction)
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

	@Subcommand("contains")
	@Description("Whether a block is 'inside' the ship")
	private fun onCheckContains(sender: Player) {
		val ship = getShip(sender) ?: return
		sender.sendRichMessage(ship.contains(sender.getTargetBlockExact(20)?.toBlockPos()).toString())
	}

	@Subcommand("thrusters")
	private fun onThrusters(sender: Player) {
		val ship = getShip(sender) ?: return
		sender.sendRichMessage("""
			Ship Heading: ${ship.heading}
			Raw Ship Thrust: ${ship.thrusters.calculateTotalThrust()}
			Actual Ship Thrust: ${ship.thrusters.calculateActualThrust(ship.heading)}
			Ship Mass: ${ship.mass}
			""".trimIndent()
		)
		for (thruster in ship.thrusters.thrusters.mapNotNull { ship.getMultiblock(it) }) {
			sender.sendRichMessage("""
				${thruster.type.displayName}
				-	Facing: ${thruster.direction}
				-	Warmup ${(thruster.data as ThrusterMultiblockData).warmupPercentage}
				""".trimIndent()
			)
		}
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

