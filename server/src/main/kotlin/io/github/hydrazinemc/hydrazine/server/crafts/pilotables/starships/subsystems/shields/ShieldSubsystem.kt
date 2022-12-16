package io.github.hydrazinemc.hydrazine.server.crafts.pilotables.starships.subsystems.shields;

import io.github.hydrazinemc.hydrazine.server.crafts.pilotables.starships.Starship
import io.github.hydrazinemc.hydrazine.server.crafts.pilotables.starships.subsystems.Subsystem
import io.github.hydrazinemc.hydrazine.server.multiblocks.MultiblockInstance
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Particle.DustOptions
import java.lang.ref.WeakReference

class ShieldSubsystem(ship: Starship) : Subsystem(ship) {
	val multiblocks = mutableSetOf<WeakReference<MultiblockInstance>>()

	var shieldHealth = 0
		private set(value) {
			field = value.coerceIn(0, maxShieldHealth)
		}

	val maxShieldHealth: Int
		get(): Int {
			var h = 0
			multiblocks.forEach { multiblock ->
				ShieldType.values().firstOrNull {
					it.multiblockType == multiblock.get()?.type
				}?.let {
					h += it.maxHealth
				}
			}
			return h
		}

	override fun onShipPiloted() {
		ship.multiblocks.forEach { multiblock ->
			if (multiblock.type in ShieldType.values().map { it.multiblockType }) {
				multiblocks.add(WeakReference(multiblock))
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
