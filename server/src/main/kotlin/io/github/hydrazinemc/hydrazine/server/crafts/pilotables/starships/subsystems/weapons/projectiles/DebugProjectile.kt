import io.github.hydrazinemc.hydrazine.server.crafts.Craft
import io.github.hydrazinemc.hydrazine.server.crafts.pilotables.starships.subsystems.weapons.projectiles.Projectile
import org.bukkit.Location
import org.bukkit.Particle

object DebugProjectile : Projectile() {
	override fun shoot(shooter: Craft, origin: Location) {
		cast(origin.clone(), 100, 40, 5,
			{
				it.world.spawnParticle(Particle.SOUL_FIRE_FLAME, it, 1, 0.0, 0.0, 0.0, 0.0, null, true)
				false
			},
			{
				false
			},
			{
				_, _ ->
				false
			},
			{
				true
			}
		)
	}
}