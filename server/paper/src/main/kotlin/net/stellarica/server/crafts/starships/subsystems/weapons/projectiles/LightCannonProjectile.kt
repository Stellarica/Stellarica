package net.stellarica.server.crafts.starships.subsystems.weapons.projectiles

import net.stellarica.server.crafts.Craft
import net.stellarica.server.utils.extensions.toBlockPos
import org.bukkit.Location
import org.bukkit.Particle

object LightCannonProjectile : Projectile() {
	override fun shoot(shooter: Craft, origin: Location) {
		cast(origin.clone(), 150, 120, 5,
			{
				false
			},
			{
				it.world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, it, 1, 0.0, 0.0, 0.0, 0.0, null, true)
				false
			},
			{ _, craft ->
				craft == shooter
			},
			{
				if (!shooter.contains(it.hitBlock?.toBlockPos())) // no ship suicide
					it.hitBlock?.location?.createExplosion(2f, false, true)
				true
			}
		)
	}
}
