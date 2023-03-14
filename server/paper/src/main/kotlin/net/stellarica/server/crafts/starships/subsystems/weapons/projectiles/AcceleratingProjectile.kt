package net.stellarica.server.crafts.starships.subsystems.weapons.projectiles;

import net.stellarica.server.crafts.Craft
import net.stellarica.server.utils.extensions.toBlockPos
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.util.RayTraceResult

class AcceleratingProjectile(override val time: Int, private val initialSpeed: Double, private val acceleration: Double):
Projectile<AcceleratingProjectile.AcceleratingProjectileData> {

	override val density = 5

	override fun shoot(shooter: Craft, origin: Location) {
		cast(origin, AcceleratingProjectileData(shooter, initialSpeed))
	}

	override fun onLocationStep(data: AcceleratingProjectileData, loc: Location) {
		loc.world.spawnParticle(Particle.SOUL_FIRE_FLAME, loc, 1, 0.0, 0.0, 0.0, 0.0, null, true)
	}

	override fun onServerTick(data: AcceleratingProjectileData, loc: Location): Double {
		loc.world.spawnParticle(Particle.FLAME, loc, 1, 0.0, 0.0, 0.0, 0.0, null, true)
		data.speed += acceleration
		return data.speed
	}

	override fun onHitCraft(data: AcceleratingProjectileData, loc: Location, craft: Craft): Boolean {
		return false
	}

	override fun onHitBlockOrEntity(data: AcceleratingProjectileData, res: RayTraceResult): Boolean {
		if (!data.shooter.contains(res.hitBlock?.toBlockPos())) // no ship suicide
			res.hitBlock?.location?.createExplosion(2f, false, true)
		return false
	}

	data class AcceleratingProjectileData(val shooter: Craft, var speed: Double)
}
