package net.stellarica.server.projectile

interface Display {
	fun update(p: Projectile)
	fun onDeath(p: Projectile)
}
