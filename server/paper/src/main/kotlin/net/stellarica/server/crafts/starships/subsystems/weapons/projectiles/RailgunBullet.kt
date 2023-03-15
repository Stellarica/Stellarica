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
		for (n in 0..50) cast(origin, InstantProjectileData(shooter))
	}

	override fun onHitBlockOrEntity(data: InstantProjectileData, res: RayTraceResult): Boolean {
		if (!data.shooter.contains(res.hitBlock?.toBlockPos())) {
			// no ship suicide
			res.hitBlock?.location?.createExplosion(1.5f, false, true)
			res.hitBlock?.location?.block?.breakNaturally()
		}
			return false
	}

	override fun onHitCraft(data: InstantProjectileData, loc: Location, craft: Craft): Boolean {
		return false
	} //no touchy

	override fun onServerTick(data: InstantProjectileData, loc: Location): Double {
		return if (data.hasShot) -1.0 else range.toDouble().also { data.hasShot = true }
	} //no touchy

	override fun onLocationStep(data: InstantProjectileData, loc: Location) {
		loc.world.spawnParticle(Particle.SWEEP_ATTACK, loc, 1, 0.0, 0.0, 0.0, 0.0, null, true)
	}
}