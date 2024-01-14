package net.stellarica.server.projectile

import net.stellarica.server.event.listen
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.util.RayTraceResult
import org.joml.Vector3d

class DebugControl(val speed: Double, val life: Int): Control {
	override fun update(p: Projectile): Projectile.ProjectileUpdate {
		p.direction.normalize() // AAA MUTABLE STATE AAAA

		return Projectile.ProjectileUpdate(
				p.direction.let { Vector3d(it.x, it.y, it.z) }.mul(speed),
				p.direction,
				true,
				p.ticksAlive < life
		)
	}

	override fun collision(p: Projectile, r: RayTraceResult): Boolean {
		p.world.world.createExplosion(p.position.x, p.position.y, p.position.z, 1.0f, false, true)
		return false;
	}
}

class DebugDisplay(): Display {
	override fun update(p: Projectile) {
		p.world.world.spawnParticle(Particle.SOUL_FIRE_FLAME, p.position.x, p.position.y, p.position.z, 1, 0.0, 0.0, 0.0, 0.0)
	}
}

fun aaaa() {
	listen<PlayerInteractEvent>({ event ->
		if (event.player.inventory.itemInMainHand.type != Material.BONE || event.action != Action.RIGHT_CLICK_AIR) return@listen
		val p = Projectile(DebugControl(0.5, 20), DebugDisplay())
		p.launch((event.player.world as CraftWorld).handle, Vector3d(event.player.location.x, event.player.location.y, event.player.location.z), Vector3d(event.player.location.direction.x, event.player.location.direction.y, event.player.location.direction.z))
	})
}