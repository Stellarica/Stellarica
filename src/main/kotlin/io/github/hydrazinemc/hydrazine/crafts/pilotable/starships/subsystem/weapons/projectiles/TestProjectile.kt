package io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.subsystem.weapons.projectiles

import io.github.hydrazinemc.hydrazine.crafts.Craft
import org.bukkit.Location
import org.bukkit.Particle

object TestProjectile : Projectile() {
	override fun shoot(shooter: Craft, origin: Location) {
		cast(origin.clone(), 100, 40, 5,
			{
				it.world.spawnParticle(Particle.FLAME, it, 1, 0.0, 0.0, 0.0, 0.0, null, true)
				false
			},
			{
				it.world.spawnParticle(Particle.SOUL_FIRE_FLAME, it, 1, 0.0, 0.0, 0.0, 0.0, null, true)
				false
			},
			{ loc, craft ->
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