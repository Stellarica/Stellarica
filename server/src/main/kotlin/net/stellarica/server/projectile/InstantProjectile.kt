package net.stellarica.server.projectile

import net.stellarica.server.craft.Craft
import net.stellarica.server.util.extension.toBlockPos
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.LivingEntity
import org.bukkit.util.RayTraceResult

open class InstantProjectile(
	private val explosionPower: Float,
	private val entityDamage: Double,

	private val particle: Particle,
	private val sound: Sound,

	private val range: Int,

	private val redstoneParticleData: Particle.DustOptions? = null
) : Projectile<InstantProjectile.InstantProjectileData> {

	data class InstantProjectileData(val shooter: Any, var hasShot: Boolean = false)

	override val time = 5 // value is basically ignored
	override val density = 5

	override fun shoot(shooter: Craft, origin: Location) {
		shootA(shooter, origin)
	}

	override fun shoot(shooter: LivingEntity, origin: Location) {
		shootA(shooter, origin)
	}

	private fun shootA(shooter: Any, origin: Location) {
		origin.world.playSound(origin, sound, SoundCategory.HOSTILE, 2.0f, 0f)
		cast(origin, InstantProjectileData(shooter))
	}

	override fun onHitBlockOrEntity(data: InstantProjectileData, res: RayTraceResult): Boolean {
		if (data.shooter is Craft) {
			if (data.shooter.contains(res.hitBlock?.toBlockPos())) return false;// no ship suicide
		} else if (data.shooter is LivingEntity) {
			if (res.hitEntity == data.shooter) return false
			else (res.hitEntity as? LivingEntity)?.damage(entityDamage)
		}
		if (explosionPower > 0) res.hitBlock?.location?.createExplosion(explosionPower, false, true)
		return false
	}

	override fun onHitCraft(data: InstantProjectileData, loc: Location, craft: Craft): Boolean {
		return false
	}

	override fun onServerTick(data: InstantProjectileData, loc: Location): Double {
		return if (data.hasShot) -1.0 else range.toDouble().also { data.hasShot = true }
	}

	override fun onLocationStep(data: InstantProjectileData, loc: Location) {
		loc.world.spawnParticle(particle, loc, 1, 0.0, 0.0, 0.0, 0.0, redstoneParticleData, true)
	}
}
