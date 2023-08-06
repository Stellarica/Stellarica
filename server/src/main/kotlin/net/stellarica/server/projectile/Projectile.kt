package net.stellarica.server.projectile

import net.minecraft.world.phys.Vec3
import org.bukkit.World

abstract class Projectile(val world: World, val startPos: Vec3) {
	abstract fun tick()
}