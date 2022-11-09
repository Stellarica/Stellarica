package io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.subsystem.weapons.projectiles

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
			{
				it.hitBlock?.type = Material.LAVA
				true
			}
		)
	}
}