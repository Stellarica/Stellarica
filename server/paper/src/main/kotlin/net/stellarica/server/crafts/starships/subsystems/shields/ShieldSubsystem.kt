package net.stellarica.server.crafts.starships.subsystems.shields

import net.stellarica.common.utils.OriginRelative
import net.stellarica.server.crafts.starships.Starship
import net.stellarica.server.crafts.starships.subsystems.Subsystem
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Particle.DustOptions

class ShieldSubsystem(ship: Starship) : Subsystem(ship) {
	val multiblocks = mutableSetOf<OriginRelative>()

	var shieldHealth = 0
		private set(value) {
			field = value.coerceIn(0, maxShieldHealth)
		}

	val maxShieldHealth: Int
		get(): Int {
			var h = 0
			multiblocks.forEach { multiblock ->
				ShieldType.values().firstOrNull {
					it.multiblock == ship.getMultiblock(multiblock)?.type
				}?.let {
					h += it.maxHealth
				}
			}
			return h
		}

	override fun onShipPiloted() {
		ship.multiblocks.forEach { multiblock ->
			if (ship.getMultiblock(multiblock)?.type in ShieldType.values().map { it.multiblock }) {
				multiblocks.add(multiblock)
			}
		}
		shieldHealth = maxShieldHealth
	}

	fun damage(loc: Location, dam: Int) {
		// todo: stuff
		loc.world.spawnParticle(Particle.REDSTONE, loc, 3, 0.5, 0.5, 0.5, 0.0, DustOptions(Color.BLUE, 3f), true)
		shieldHealth -= dam
	}
}
