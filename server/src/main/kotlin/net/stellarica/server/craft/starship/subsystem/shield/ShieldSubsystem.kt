package net.stellarica.server.craft.starship.subsystem.shield

import net.stellarica.common.util.OriginRelative
import net.stellarica.server.craft.starship.Starship
import net.stellarica.server.craft.starship.subsystem.Subsystem
import net.stellarica.server.util.extension.asMiniMessage
import net.stellarica.server.util.extension.sendRichActionBar
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Particle.DustOptions
import kotlin.math.roundToInt

class ShieldSubsystem(ship: Starship) : Subsystem(ship) {
	val multiblocks = mutableSetOf<OriginRelative>()

	var shieldHealth = 0
		private set(value) {
			field = value.coerceIn(0, maxShieldHealth)
		}

	val maxShieldHealth: Int
		get(): Int {
			var h = 0
			for (multiblock in multiblocks) {
				ShieldType.values().firstOrNull {
					it.multiblock == ship.getMultiblock(multiblock)?.type
				}?.let {
					h += it.maxHealth
				}
			}
			return h
		}

	override fun onShipPiloted() {
		for (multiblock in ship.multiblocks) {
			if (ship.getMultiblock(multiblock)?.type in ShieldType.values().map { it.multiblock }) {
				multiblocks.add(multiblock)
			}
		}
		shieldHealth = maxShieldHealth
	}

	override fun onShipTick() {
		val percent = shieldHealth /maxShieldHealth.toFloat().coerceAtLeast(1f)
		val num = (percent * 40).roundToInt().coerceAtMost(40)

		ship.pilot?.sendRichActionBar("<dark_gray>[<gray>" + "|".repeat(40 - num) + "<blue>|".repeat(num) + "<dark_gray>]")
	}

	fun damage(loc: Location, dam: Int) {
		// todo: stuff
		loc.world.spawnParticle(Particle.REDSTONE, loc, 3, 0.5, 0.5, 0.5, 0.0, DustOptions(Color.AQUA, 3f), true)
		shieldHealth -= dam
	}
}
