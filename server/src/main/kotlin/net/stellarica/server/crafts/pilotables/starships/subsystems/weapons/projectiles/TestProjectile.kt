package net.stellarica.server.crafts.pilotables.starships.subsystems.weapons.projectiles

import net.stellarica.server.crafts.Craft
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