package io.github.hydrazinemc.hydrazine.server.crafts.pilotables.starships.subsystems.weapons.projectiles

import io.github.hydrazinemc.hydrazine.server.crafts.Craft
import org.bukkit.Location
import org.bukkit.Particle

object LightCannonProjectile : Projectile() {
	override fun shoot(shooter: Craft, origin: Location) {
		cast(origin.clone(), 150, 120, 5,
			{
				it.world.spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, it, 1, 0.0, 0.0, 0.0, 0.0, null, true)
				false
			},
			{ _, craft ->
				craft == shooter
			},
			{
				if (!shooter.contains(it.hitBlock?.location)) // no ship suicide
					it.hitBlock?.location?.createExplosion(2f, false, true)
				true
			}
		)
	}
}
