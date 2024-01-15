package net.stellarica.server.projectile

import net.stellarica.server.event.listen
import net.stellarica.server.util.wrapper.ServerWorld
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.util.RayTraceResult
import org.joml.Vector3d

class DebugControl(val speed: Double, val life: Int) : Control {
	override fun update(p: Projectile): Projectile.ProjectileUpdate {
		return Projectile.ProjectileUpdate(
			Vector3d(p.direction).normalize().mul(speed),
			p.direction,
			true,
			p.ticksAlive < life
		)
	}

	override fun collision(p: Projectile, r: RayTraceResult): Boolean {
		if (p.origin.distance(p.position) < 1.0) return true
		p.world.bukkit.createExplosion(p.position.x, p.position.y, p.position.z, 1.0f, false, true)
		return false;
	}
}

class DebugDisplay() : Display {
	override fun update(p: Projectile) {
		p.world.bukkit.spawnParticle(Particle.SOUL_FIRE_FLAME, p.position.x, p.position.y, p.position.z, 1, 0.0, 0.0, 0.0, 0.0)
	}

	override fun onDeath(p: Projectile) {
	}
}

fun aaaa() {
	listen<PlayerInteractEvent>({ event ->
		if (event.player.inventory.itemInMainHand.type != Material.BONE || (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK)) return@listen
		val p = Projectile(DebugControl(0.8, 40), DebugDisplay())
		p.launch(ServerWorld(event.player.world), Vector3d(event.player.location.x, event.player.location.y, event.player.location.z), Vector3d(event.player.location.direction.x, event.player.location.direction.y, event.player.location.direction.z))
	})
}
