package net.stellarica.server.projectile

import net.stellarica.server.util.Tasks
import net.stellarica.server.util.wrapper.ServerWorld
import org.bukkit.FluidCollisionMode
import org.bukkit.Location
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import org.joml.Vector3d

class Projectile(val control: Control, val display: Display) {
	lateinit var world: ServerWorld
		private set
	lateinit var position: Vector3d
		private set
	lateinit var direction: Vector3d
		private set
	lateinit var origin: Vector3d
		private set

	var ticksAlive = 0
		private set

	data class ProjectileUpdate(
		val delta: Vector3d,
		val direction: Vector3d,
		val doRaycast: Boolean,
		val isAlive: Boolean
	)

	fun launch(world: ServerWorld, position: Vector3d, direction: Vector3d) {
		if (ticksAlive != 0) throw IllegalStateException("Projectile already launched")
		this.world = world
		this.position = Vector3d(position)
		this.origin = Vector3d(position)
		this.direction = Vector3d(direction)

		Tasks.syncRepeat(1, 1) {
			update(this)
		}
	}

	private fun update(r: BukkitRunnable) {
		val data = control.update(this@Projectile)

		if (!data.isAlive) {
			die(r)
			return
		}

		if (data.doRaycast) {
			val dist = data.delta.length()
			val res = world.bukkit.rayTrace(
				Location(world.bukkit, position.x, position.y, position.z),
				data.delta.let { Vector(it.x, it.y, it.z) }.normalize(),
				dist,
				FluidCollisionMode.NEVER,
				true,
				0.1,
				{ true }
			)
			if (res != null && (res.hitBlock != null || res.hitEntity != null)) {
				if (!control.collision(this@Projectile, res)) {
					die(r)
					return
				}
			}
		}

		position.add(data.delta)
		direction.set(data.direction)

		display.update(this@Projectile)
		ticksAlive++
	}

	private fun die(r: BukkitRunnable) {
		display.onDeath(this)
		r.cancel()
	}
}
