package io.github.petercrawley.minecraftstarshipplugin.projectiles

import com.destroystokyo.paper.ParticleBuilder
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.getPlugin
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.LivingEntity
import org.bukkit.util.Vector

class ParticleProjectile (val origin: Location, private val color: Color, private val particleDensity: Int,
                          val range: Int, private val minRange: Double, val speed: Int,
                          private val damage: Double, private val explosion: Float) // Yikes that's a lot of arguments
{
    private val particle = ParticleBuilder(Particle.REDSTONE).color(color).force(true).count(particleDensity)
    // ParticleBuilder that we can spawn later when we need to

    fun shootBeam() {
        // Simple beam mode, do it all on the same server tick
        val loc = origin.clone() // Don't want to modify the origin, so we clone it
        val direction: Vector = origin.direction // Main reason this uses Locations (even though they are slow) is for this
        // Basically represent the amount we move every step
        direction.multiply(range)
        direction.normalize()

        for (i in 0..range) { // Go tick at every location in the range, during this server tick
            loc.add(direction)
            if (!tick(loc)) break // if it returned false it hit something
        }
    }

    fun shootProjectile() {
        // Start the Runnable that will handle projectile mode
        ProjectileRunnable(this).runTaskTimer(getPlugin(), 1, 1)
    }

    fun tick(loc: Location): Boolean{
        // A single particle spawn + check for damage/blocks/explosion
        // Returns false if it hit something, otherwise true.
        particle.location(loc).spawn() // actually spawn the particle

        for (e in loc.world.getNearbyEntities(loc, 1.0, 1.0, 1.0)) {
            if (e is LivingEntity && origin.distance(loc) > minRange) {
                // minRange exists so the person shooting (if its a gun or something)
                // or the ship shooting don't blow themselves to bits. Its not a great system,
                // but it works. We can't just exclude the player that shot it because we don't know if it *is* a
                // player who shot it.
                e.damage(damage)
                if (explosion > 0) loc.world.createExplosion(loc, explosion)
                return false // damage one entity only
            }
        }

        if (loc.block.type != Material.AIR) {
            if (explosion > 0) { // Sure this could be one line but checking distance is costly and we should only do it if we have to
                if (origin.distance(loc) > minRange) { // distance() might return NaN if the distance is too high...
                    loc.world.createExplosion(loc, explosion) // As far as I can tell this is the main cause of lag when spamming these
                }
            }
            return false
        }
        return true
    }
}