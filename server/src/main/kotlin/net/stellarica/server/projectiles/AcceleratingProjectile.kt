package net.stellarica.server.projectiles;

import net.stellarica.server.crafts.Craft
import net.stellarica.server.utils.extensions.toBlockPos
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.LivingEntity
import org.bukkit.util.RayTraceResult

class AcceleratingProjectile(
	private val explosionPower: Float,
	private val entityDamage: Double,

	private val particle: Particle,
	private val sound: Sound,

	override val time: Int,
	private val initialSpeed: Double,
	private val acceleration: Double,

	private val redstoneParticleData: Particle.DustOptions? = null
):
	Projectile<AcceleratingProjectile.AcceleratingProjectileData> {

	override val density = 5

	override fun shoot(shooter: Craft, origin: Location) {
		shootA(shooter, origin)
	}

	override fun shoot(shooter: LivingEntity, origin: Location) {
		shootA(shooter, origin)
	}
	private fun shootA(shooter: Any, origin: Location) {
		origin.world.playSound(origin, sound, SoundCategory.HOSTILE, 2.0f, 0f)
		cast(origin, AcceleratingProjectileData(shooter, initialSpeed))
	}

	override fun onLocationStep(data: AcceleratingProjectileData, loc: Location) {
		loc.world.spawnParticle(particle, loc, 1, 0.0, 0.0, 0.0, 0.0, redstoneParticleData, true)
	}

	override fun onServerTick(data: AcceleratingProjectileData, loc: Location): Double {
		data.speed += acceleration
		return data.speed
	}

	override fun onHitCraft(data: AcceleratingProjectileData, loc: Location, craft: Craft): Boolean {
		return false
	}

	override fun onHitBlockOrEntity(data: AcceleratingProjectileData, res: RayTraceResult): Boolean {
		if (data.shooter is Craft) {
			if (data.shooter.contains(res.hitBlock?.toBlockPos())) return false;// no ship suicide
		} else if (data.shooter is LivingEntity) {
			if (res.hitEntity == data.shooter) return false
			else (res.hitEntity as? LivingEntity)?.damage(entityDamage)
		}
		if (explosionPower > 0) res.hitBlock?.location?.createExplosion(explosionPower, false, true)
		return false
	}

	data class AcceleratingProjectileData(val shooter: Any, var speed: Double)
}
