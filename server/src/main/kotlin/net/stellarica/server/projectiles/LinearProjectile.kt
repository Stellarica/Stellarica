package net.stellarica.server.projectiles;

import net.stellarica.server.crafts.Craft
import net.stellarica.server.utils.extensions.toBlockPos
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.util.RayTraceResult

class LinearProjectile(
	private val explosionPower: Float,
	private val particle: Particle,
	private val sound: Sound,

	override val time: Int,
	private val speed: Double,

	private val redstoneParticleData: Particle.DustOptions? = null
):
	Projectile<LinearProjectile.LinearProjectileData> {

	override val density = 5

	override fun shoot(shooter: Craft, origin: Location) {
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
		if (!data.shooter.contains(res.hitBlock?.toBlockPos())) // no ship suicide
			res.hitBlock?.location?.createExplosion(explosionPower, false, true)
		return false
	}

	data class LinearProjectileData(val shooter: Craft)
}
