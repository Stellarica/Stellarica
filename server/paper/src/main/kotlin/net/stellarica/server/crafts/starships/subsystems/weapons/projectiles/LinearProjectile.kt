package net.stellarica.server.crafts.starships.subsystems.weapons.projectiles;

import com.destroystokyo.paper.ParticleBuilder
import net.stellarica.server.crafts.Craft
import net.stellarica.server.utils.extensions.toBlockPos
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.util.RayTraceResult

class LinearProjectile(override val time: Int, private val speed: Double):
	Projectile<LinearProjectile.LinearProjectileData> {

	override val density = 5

	override fun shoot(shooter: Craft, origin: Location) {
		origin.world.playSound(origin, Sound.ENTITY_BEE_HURT, SoundCategory.HOSTILE, 0.8f, 0f)
		cast(origin, LinearProjectileData(shooter))
	}

	override fun onLocationStep(data: LinearProjectileData, loc: Location) {
		loc.world.spawnParticle(Particle.WATER_BUBBLE, loc, 1, 0.0, 0.0, 0.0, 0.0, null, true)
	}

	override fun onServerTick(data: LinearProjectileData, loc: Location): Double {
		//loc.world.spawnParticle(Particle.FLAME, loc, 1, 0.0, 0.0, 0.0, 0.0, null, true)
		return speed
	}

	override fun onHitCraft(data: LinearProjectileData, loc: Location, craft: Craft): Boolean {
		return false
	}

	override fun onHitBlockOrEntity(data: LinearProjectileData, res: RayTraceResult): Boolean {
		if (!data.shooter.contains(res.hitBlock?.toBlockPos())) // no ship suicide
			res.hitBlock?.location?.createExplosion(2f, false, true)
		return false
	}

	data class LinearProjectileData(val shooter: Craft)
}
