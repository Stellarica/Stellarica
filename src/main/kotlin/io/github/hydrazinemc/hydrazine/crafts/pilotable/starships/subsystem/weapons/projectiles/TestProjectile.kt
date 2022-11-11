package io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.subsystem.weapons.projectiles

import io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.Starship
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle

object TestProjectile: Projectile() {
	override fun shoot(origin: Location) {
		cast(origin.clone(), 100, 40, 5,
			{
				it.world.spawnParticle(Particle.FLAME, it, 1, 0.0, 0.0, 0.0, 0.0, null, true)
				false
			},
			{
				it.world.spawnParticle(Particle.SOUL_FIRE_FLAME, it, 1, 0.0, 0.0, 0.0, 0.0, null, true)
				false
			},
			{loc, craft ->
				/*
				val ship = craft as? Starship ?: return@cast false // todo: bad for npc ships
				ship.shields.damage(loc, 10)

				ship.shields.shieldHealth > 0
				 */
				false
			},
			{
				it.hitBlock?.location?.createExplosion(2f, true, true)
				true
			}
		)
	}
}