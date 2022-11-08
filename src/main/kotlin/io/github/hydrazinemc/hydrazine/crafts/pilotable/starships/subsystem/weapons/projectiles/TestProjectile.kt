package io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.subsystem.weapons.projectiles

import org.bukkit.Location
import org.bukkit.Particle

object TestProjectile: Projectile {
	override fun shoot(origin: Location) {
		origin.add(0.5, 0.5, 0.5)
		origin.world.rayTraceBlocks(origin.clone().add(origin.direction), origin.direction, 100.0)?.let {
			val loc = it.hitPosition.toLocation(origin.world)
			origin.world.createExplosion(loc, 5.0f)
			loc.world.spawnParticle(Particle.SOUL_FIRE_FLAME, loc, 1, 0.0, 0.0, 0.0, 0.0, null, true)

			val particleLoc = origin.clone()
			val path = loc.clone().subtract(origin)
			val step = path.clone().multiply(1.0 / path.length())
			for (i in 1..path.length().toInt()) {
				particleLoc.add(step)
				particleLoc.world.spawnParticle(Particle.SOUL_FIRE_FLAME, particleLoc, 1, 0.0, 0.0, 0.0, 0.0, null, true)
			}
		}
	}
}