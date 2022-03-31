package io.github.hydrazinemc.hydrazine.projectiles

import com.destroystokyo.paper.ParticleBuilder
import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.plugin
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.LivingEntity
import org.bukkit.util.Vector

// TODO: use raycasts
data class ParticleProjectile(
	val origin: Location, private val color: Color, private val particleDensity: Int,
	val range: Int, private val minRange: Double, val speed: Int,
	private val damage: Double, private val explosion: Float
) // Yikes that's a lot of arguments
{
	private val particle = ParticleBuilder(Particle.REDSTONE).color(color).force(true).count(particleDensity)
	// ParticleBuilder that we can spawn later when we need to

	fun shootBeam() {
		// Simple beam mode, do it all on the same server tick

		// val event = ParticleProjectileLaunchEvent(this, origin)
		// getPluginManager().callEvent(event)

		val loc = origin.clone() // Don't want to modify the origin, so we clone it
		val direction: Vector =
			origin.direction // Main reason this uses Locations (even though they are slow) is for this
		// Basically represent the amount we move every step
		direction.multiply(range)
		direction.normalize()

		for (i in 0..range) { // Go step at every location in the range, during this server tick
			loc.add(direction)
			if (!step(loc)) break // if it returned false it hit something
		}
	}

	fun shootProjectile() {
		// Start the Runnable that will handle projectile mode

		// val event = ParticleProjectileLaunchEvent(this, origin)
		// getPluginManager().callEvent(event)

		ProjectileRunnable(this).runTaskTimer(plugin, 1, 1)
	}

	fun step(loc: Location): Boolean {
		// A single particle spawn + check for damage/blocks/explosion
		// Returns false if it hit something, otherwise true.
		particle.location(loc).spawn() // actually spawn the particle

		for (e in loc.world.getNearbyEntities(loc, 1.0, 1.0, 1.0)) {
			if (e is LivingEntity && origin.distanceSquared(loc) > minRange * minRange) {
				// minRange exists so the person shooting (if it's a gun or something)
				// or the ship shooting don't blow themselves to bits. It's not a great system,
				// but it works. We can't just exclude the player that shot it because we don't know if it *is* a
				// player who shot it.

				// TODO: Exclude the entire starship/player that shot the projectile, instead of dealing with minRange

				e.damage(damage)
				if (explosion > 0) loc.world.createExplosion(loc, explosion)

				// val event = ParticleProjectileHitEntityEvent(this, loc, e)
				// getPluginManager().callEvent(event)

				return false // damage one entity only
			}
		}

		if (loc.block.type.isSolid) {
			if (explosion > 0 && origin.distanceSquared(loc) > minRange * minRange) {
				loc.world.createExplosion(loc, explosion)
			}

			// Alert any listeners
			// val event = ParticleProjectileHitBlockEvent(this, loc)
			// getPluginManager().callEvent(event)

			return false
		}

		// If nothing stopped the beam, we're good to continue
		return true
	}
}