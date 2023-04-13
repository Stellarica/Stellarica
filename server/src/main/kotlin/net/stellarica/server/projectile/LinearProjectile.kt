package net.stellarica.server.projectile;

import net.stellarica.server.craft.Craft
import net.stellarica.server.util.extension.toBlockPos
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.LivingEntity
import org.bukkit.util.RayTraceResult

class LinearProjectile(
	private val explosionPower: Float,
	private val entityDamage: Double,

	private val particle: Particle,
	private val sound: Sound,

	override val time: Int,
	private val speed: Double,

	private val redstoneParticleData: Particle.DustOptions? = null
) :
	Projectile<LinearProjectile.LinearProjectileData> {

	override val density = 5

	override fun shoot(shooter: Craft, origin: Location) {
		shootA(shooter, origin)
	}

	override fun shoot(shooter: LivingEntity, origin: Location) {
		shootA(shooter, origin)
	}

	private fun shootA(shooter: Any, origin: Location) {
		origin.world.playSound(origin, sound, SoundCategory.HOSTILE, 2f, 0f)
		cast(origin, LinearProjectileData(shooter))
	}

	override fun onLocationStep(data: LinearProjectileData, loc: Location) {
		loc.world.spawnParticle(particle, loc, 1, 0.0, 0.0, 0.0, 0.0, redstoneParticleData, true)
	}

	override fun onServerTick(data: LinearProjectileData, loc: Location): Double {
		return speed
	}

	override fun onHitCraft(data: LinearProjectileData, loc: Location, craft: Craft): Boolean {
		return false
	}

	override fun onHitBlockOrEntity(data: LinearProjectileData, res: RayTraceResult): Boolean {
		if (data.shooter is Craft) {
			if (data.shooter.contains(res.hitBlock?.toBlockPos())) return false;// no ship suicide
		} else if (data.shooter is LivingEntity) {
			if (res.hitEntity == data.shooter) return false
			else (res.hitEntity as? LivingEntity)?.damage(entityDamage)
		}
		if (explosionPower > 0) res.hitBlock?.location?.createExplosion(explosionPower, false, true)
		return false
	}

	data class LinearProjectileData(val shooter: Any)
}
