package io.github.hydrazinemc.hydrazine.projectiles

import com.destroystokyo.paper.ParticleBuilder
import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.plugin
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.LivingEntity
import org.bukkit.util.Vector

// TODO: use raycasts
/**
 * Handles projectile beams that deal damage to entities and optionally explode
 */
data class ParticleProjectile(
	/**
	 * The point from which the particles shoot
	 */
	val origin: Location,
	private val color: Color,
	private val particleDensity: Int,
	/**
	 * The maximum distance (in blocks) that the particles can travel
	 */
	val range: Int,
	/**
	 * Within this range the projectiles will not deal damage
	 * Hacky solution to prevent players shooting themselves.
	 */
	private val minRange: Double,
	/**
	 * The speed at which the particles travel
	 */
	val speed: Int,
	private val damage: Double, private val explosion: Float
) // Yikes that's a lot of arguments
{
	private val particle = ParticleBuilder(Particle.REDSTONE).color(color).force(true).count(particleDensity)
	// ParticleBuilder that we can spawn later when we need to

	/**
	 * Shoot a simple beam, spawning all of the particles on the same server tick
	 * @see shootProjectile
	 */
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

	/**
	 * Shoot a slower moving group of particles.
	 * @see shootBeam
	 */
	fun shootProjectile() {
		// Start the Runnable that will handle projectile mode

		// val event = ParticleProjectileLaunchEvent(this, origin)
		// getPluginManager().callEvent(event)

		ProjectileRunnable(this).runTaskTimer(plugin, 1, 1)
	}

	/**
	 * Calculate the next particle location, and spawn the particle
	 * @return whether the projectile hit an entity or block, else false
	 */
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

				return false // damage one entity only
			}
		}

		if (loc.block.type.isSolid) {
			if (explosion > 0 && origin.distanceSquared(loc) > minRange * minRange) {
				loc.world.createExplosion(loc, explosion)
			}

			return false
		}

		// If nothing stopped the beam, we're good to continue
		return true
	}
}
