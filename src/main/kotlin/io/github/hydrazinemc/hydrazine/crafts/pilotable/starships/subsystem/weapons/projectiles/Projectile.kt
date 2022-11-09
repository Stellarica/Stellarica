package io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.subsystem.weapons.projectiles;

import io.github.hydrazinemc.hydrazine.Hydrazine
import org.bukkit.FluidCollisionMode
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.RayTraceResult

abstract class Projectile {
	abstract fun shoot(origin: Location)


	/**
	 * Handles projectile raycasts
	 */
	fun cast(
		/**
		 * The location and direction to shoot from
		 */
		origin: Location,
		/**
		 * The maximum number of blocks this projectile can travel.
		 * Speed is calculated based on this and the alive time.
		 */
		range: Int,
		/**
		 * The amount of time the projectile is active.
		 * Speed is calculated based on this and the range.
		 */
		time: Int,
		/**
		 * The number of times to call onLocationTick per block travelled per tick
		 */
		density: Int,
		/**
		 * Called every step * density times per tick, at incremental locations
		 * @return whether to continue onward
		 */
		onLocationTick: (Location) -> Boolean,
		/**
		 * Called every server tick this projectile is running
		 * @return whether to continue onward
		 */
		onServerTick: (Location) -> Boolean,
		/**
		 * Called when the projectile hits something
		 * @return whether to continue onward
		 */
		onHit: (RayTraceResult) -> Boolean
	) {
		/** the current position */
		val position = origin.clone()
		/** the distance, in blocks, to go each tick*/
		val step = range / time.toDouble()
		/** the number of ticks this has been running*/
		var count = 0

		class Runnable(): BukkitRunnable() {
			override fun run() {
				val hit = position.world.rayTrace(
					position,
					origin.direction,
					step,
					FluidCollisionMode.NEVER,
					true,
					0.1,
					null
				)
				if (hit != null) {
					if (onHit(hit)) {
						this.cancel()
						return
					}
				}


				val locStep = position.clone()
				for (i in 1..(step * density).toInt()) {
					if (onLocationTick(locStep)) {
						this.cancel()
						return
					}

					locStep.add(origin.direction.clone().multiply(1/density.toFloat()))
				}

				position.add(origin.direction.clone().multiply(step))


				if (onServerTick(position)) {
					this.cancel()
					return
				}

				count++
				if (count >= time) {
					this.cancel()
				}
			}
		}

		Runnable().runTaskTimer(Hydrazine.plugin, 0, 1)
	}
}
