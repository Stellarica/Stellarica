package io.github.petercrawley.minecraftstarshipplugin.projectiles

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.LivingEntity
import org.bukkit.util.Vector

class ParticleProjectile (val origin: Location, val particle: Particle, val range: Int, val minRange: Double, val damage: Double, val explosion: Float) {

    fun shoot(){
        val direction: Vector = origin.direction
        direction.multiply(range /* the range */)
        direction.normalize()
        val loc = origin.clone()
        for (i in 0..range) {

            loc.add(direction)
            loc.world.spawnParticle(particle, loc,2,0.0,0.0,0.0,0.0,null,true)


            for (e in loc.world.getNearbyEntities(loc, 1.0, 1.0, 1.0)) {
                if (e is LivingEntity && origin.distance(loc) > minRange) {
                    e.damage(damage)
                    loc.world.createExplosion(loc, explosion)
                    return // damage one entity only
                }
            }

            if (loc.block.type != Material.AIR) {
                if (explosion > 0) {
                    if (origin.distance(loc) > minRange) {// don't want it exploding where it was shot from, but don't want to spam distance() as it's costly.
                        // distance() might return NaN if the distance is too high...
                        loc.world.createExplosion(loc, explosion)
                    }
                }
                return
            }
        }
    }
}