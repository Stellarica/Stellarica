package net.stellarica.server.crafts.starships.subsystems.weapons.projectiles

import net.stellarica.server.crafts.Craft
import net.stellarica.server.utils.extensions.toBlockPos
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.util.RayTraceResult

class InstantProjectile(private val range: Int): Projectile<InstantProjectile.InstantProjectileData> {

	data class InstantProjectileData(val shooter: Craft, var hasShot: Boolean = false)

	override val time = 5 // value is basically ignored
	override val density = 5

	override fun shoot(shooter: Craft, origin: Location) {
		origin.world.playSound(origin, Sound.ENTITY_BEE_HURT, SoundCategory.HOSTILE, 0.8f, 1f)
		cast(origin, InstantProjectileData(shooter))
	}

	override fun onHitBlockOrEntity(data: InstantProjectileData, res: RayTraceResult): Boolean {
		if (!data.shooter.contains(res.hitBlock?.toBlockPos())) // no ship suicide
			res.hitBlock?.location?.createExplosion(2f, false, true)
		return false
	}

	override fun onHitCraft(data: InstantProjectileData, loc: Location, craft: Craft): Boolean {
		return false
	}

	override fun onServerTick(data: InstantProjectileData, loc: Location): Double {
		//loc.world.spawnParticle(Particle.FLAME, loc, 1, 0.0, 0.0, 0.0, 0.0, null, true)
		return if (data.hasShot) -1.0 else range.toDouble().also { data.hasShot = true }
	}

	override fun onLocationStep(data: InstantProjectileData, loc: Location) {
		loc.world.spawnParticle(Particle.FLAME, loc, 1, 0.0, 0.0, 0.0, 0.0, null, true)
	}
}