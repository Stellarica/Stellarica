package io.github.hydrazinemc.hydrazine.projectiles

import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

/**
 * Bukkit runnable that handles particle projectiles
 * @see ParticleProjectile
 */
class ProjectileRunnable(private val projectileHandler: ParticleProjectile) : BukkitRunnable() {
	/**
	 * How far the projectile has travelled
	 */
	private var counter = 0

	/**
	 * The current location of the projectile
	 */
	private var loc = projectileHandler.origin.clone() // Don't want to modify the origin

	private val direction: Vector = projectileHandler.origin.direction.multiply(projectileHandler.range).normalize()
	// The amount that we change the location by every step

	/**
	 * Move the projectile.
	 * Should not be called manually as it is part of a Bukkit runnable.
	 */
	override fun run() {

		for (i in 0..projectileHandler.speed) {
			// We have to do the speed number of steps per tick, otherwise it's incredibly slow

			// Counter is basically how far are we toward the max range.
			// We want to cancel when we reach that or else... big lag and eventually errors
			if (counter > projectileHandler.range) {
				cancel()
				break
			}

			loc.add(direction) // Move forward

			if (!projectileHandler.step(loc)) { // Spawn the particle and check for blocks/entities
				// it will be false if it hit something, so don't do any more steps
				cancel()
				break
			}
			counter++
		}
	}
}
