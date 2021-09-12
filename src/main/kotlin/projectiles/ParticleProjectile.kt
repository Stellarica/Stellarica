package io.github.petercrawley.minecraftstarshipplugin.projectiles

import com.destroystokyo.paper.ParticleBuilder
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.getPlugin
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.LivingEntity
import org.bukkit.util.Vector

class ParticleProjectile (val origin: Location, val color: Color, val range: Int, val minRange: Double, val speed: Int, val damage: Double, val explosion: Float) {
    private val particle = ParticleBuilder(Particle.REDSTONE).color(color).force(true).count(2)

    fun shootBeam() {
        val loc = origin.clone()
        val direction: Vector = origin.direction
        direction.multiply(range)
        direction.normalize()
        // Simple beam mode, do it all on the same server tick
        for (i in 0..range) {
            loc.add(direction)
            if (!tick(loc)) break // if it returned false it hit something
        }
    }

    fun shootProjectile() {
        ProjectileRunnable(this).runTaskTimer(getPlugin(), 1, 1)
    }

    fun tick(loc: Location): Boolean{
        particle.location(loc).spawn()

        for (e in loc.world.getNearbyEntities(loc, 1.0, 1.0, 1.0)) {
            if (e is LivingEntity && origin.distance(loc) > minRange) {
                e.damage(damage)
                if (explosion > 0) loc.world.createExplosion(loc, explosion)
                return false // damage one entity only
            }
        }

        if (loc.block.type != Material.AIR) {
            if (explosion > 0) {
                if (origin.distance(loc) > minRange) {// don't want it exploding where it was shot from, but don't want to spam distance() as it's costly.
                    // distance() might return NaN if the distance is too high...
                    loc.world.createExplosion(loc, explosion)
                }
            }
            return false
        }
        return true
    }
}