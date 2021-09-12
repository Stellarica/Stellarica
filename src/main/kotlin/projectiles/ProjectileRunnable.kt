package io.github.petercrawley.minecraftstarshipplugin.projectiles

import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

class ProjectileRunnable(private val projectileHandler: ParticleProjectile) : BukkitRunnable() {
    private var counter = 0
    private var loc = projectileHandler.origin.clone() // Don't want to modify the origin
    private val direction: Vector = projectileHandler.origin.direction.multiply(projectileHandler.range).normalize()
    // The amount that we change the location by every step

    override fun run() {
        if (counter > projectileHandler.range) cancel()
        // Counter is basically how far are we toward the max range.
        // We want to cancel when we reach that or else... big lag and eventually errors

        for (i in 0..projectileHandler.speed){ // We have to do the speed number of steps per tick, otherwise its incredibly slow
            loc.add(direction)

            if (!projectileHandler.tick(loc)) { // Spawn the particle and check for blocks/entities
                // it will be false if it hit something, so don't do any more steps
                cancel()
                break
            }
            counter++
        }
    }
}