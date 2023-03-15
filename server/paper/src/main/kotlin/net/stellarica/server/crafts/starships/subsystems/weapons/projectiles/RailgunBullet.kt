package net.stellarica.server.crafts.starships.subsystems.weapons.projectiles

import net.minecraft.world.level.block.AirBlock
import net.stellarica.server.crafts.Craft
import net.stellarica.server.utils.extensions.toBlockPos
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.util.RayTraceResult

class RailgunBullet(private val range: Int): Projectile<RailgunBullet.InstantProjectileData> {

	data class InstantProjectileData(val shooter: Craft, var hasShot: Boolean = false)

	override val time = 5 // value is basically ignored
	override val density = 1

	override fun shoot(shooter: Craft, origin: Location) {
		cast(origin, InstantProjectileData(shooter))
	}

	override fun onHitBlockOrEntity(data: InstantProjectileData, res: RayTraceResult): Boolean {
		if (!data.shooter.contains(res.hitBlock?.toBlockPos())) // no ship suicide
			res.hitBlock?.location?.createExplosion(1f, false, true)
			res.hitBlock?.location?.block?.breakNaturally()
			fun shoot(shooter: Craft, origin: Location) {
				cast(origin, InstantProjectileData(shooter))
			}
		return true
	}

	override fun onHitCraft(data: InstantProjectileData, loc: Location, craft: Craft): Boolean {
		return false
	}

	override fun onServerTick(data: InstantProjectileData, loc: Location): Double {
		loc.world.spawnParticle(Particle.EXPLOSION_NORMAL, loc, 1, 0.0, 0.0, 0.0, 0.0, null, true)
		return if (data.hasShot) -1.0 else range.toDouble().also { data.hasShot = true }
	}

	override fun onLocationStep(data: InstantProjectileData, loc: Location) {
		if (!data.shooter.contains(loc.toBlockPos()))  // no ship suicide + no mid air explosions
		loc.world.spawnParticle(Particle.SWEEP_ATTACK, loc, 1, 0.0, 0.0, 0.0, 0.0, null, true);
	}
}