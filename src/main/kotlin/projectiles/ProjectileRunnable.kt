package io.github.petercrawley.minecraftstarshipplugin.projectiles

import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

class ProjectileRunnable(private val projectileHandler: ParticleProjectile) : BukkitRunnable() {
    private var counter = 0
    private var loc = projectileHandler.origin.clone()
    private val direction: Vector = projectileHandler.origin.direction.multiply(projectileHandler.range).normalize()

    override fun run() {
        if (counter > projectileHandler.range) cancel()

        for (i in 0..projectileHandler.speed){
            loc.add(direction)
            if (!projectileHandler.tick(loc)) {
                // it will be false if it hit something, so don't do any more steps
                cancel()
                break
            }
            counter++
        }
    }
}