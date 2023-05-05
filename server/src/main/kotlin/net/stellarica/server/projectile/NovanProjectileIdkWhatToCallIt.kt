package net.stellarica.server.projectile

import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound

class NovanProjectileIdkWhatToCallIt(
		val muzzleFlash: Float,
		explosionPower: Float,
		entityDamage: Double,
		particle: Particle,
		sound: Sound,
		time: Int,
		speed: Double,
		redstoneParticleData: Particle.DustOptions? = null
) : LinearProjectile(explosionPower, entityDamage, particle, sound, time, speed, redstoneParticleData) {
	override fun shootA(shooter: Any, origin: Location) {
		super.shootA(shooter, origin)
		origin.world.createExplosion(origin, muzzleFlash, false, false)
	}
}