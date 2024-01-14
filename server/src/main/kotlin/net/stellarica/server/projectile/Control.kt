package net.stellarica.server.projectile

import org.bukkit.util.RayTraceResult

interface Control {
	fun update(p: Projectile): Projectile.ProjectileUpdate

	/** Returns true if the collision should be ignored */
	fun collision(p: Projectile, r: RayTraceResult): Boolean
}