package io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.subsystem.weapons.projectiles;

import io.github.hydrazinemc.hydrazine.Hydrazine
import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.pilotedCrafts
import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.plugin
import io.github.hydrazinemc.hydrazine.crafts.Craft
import org.bukkit.FluidCollisionMode
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.RayTraceResult

abstract class Projectile {
	abstract fun shoot(shooter: Craft, origin: Location)


	// I admit, this projectile thing is a little funky
	// However, having each of these function arguments as a method on Projectile is a lot less flexible, as
	// the ability to have any instance data would require having different instances of projectiles,
	// which makes passing around a projectile type a pain.
	// It's totally doable, but this works too so... deal with it. :)
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
		onHitCraft: (Location, Craft) -> Boolean,
		/**
		 * Called when the projectile hits something
		 * @return whether to continue onward
		 */
		onHitBlockOrEntity: (RayTraceResult) -> Boolean
	) {
		/** the current position */
		val position = origin.clone()
		/** the distance, in blocks, to go each tick*/
		val step = range / time.toDouble()
		/** the number of ticks this has been running*/
		var count = 0

		class Runnable(): BukkitRunnable() {
			// gets run every tick
			override fun run() {
				// ray trace as far as it moves this tick (step blocks)
				val hit = position.world.rayTrace(
					position,
					origin.direction,
					step,
					FluidCollisionMode.NEVER,
					true,
					0.1,
					null
				)
				// if it hit something, call the appropriate function
				// continue on if it returns false
				if (hit != null) {
					val hitLoc = hit.hitPosition.toLocation(position.world)
					// first, check if we hit a starship
					// TODO: check all ships, not just pilotable ones (need to hit npc ships)
					// TODO: use a better filter than distancequared
					pilotedCrafts.filter{it.origin.world == position.world}.filter {it.origin.asLocation.distanceSquared(position) < 1000}.forEach {
						if (it.contains(hitLoc)) {
							if (onHitCraft(hitLoc, it)) {
								this.cancel()
								return
							}
						}
					}

					if (onHitBlockOrEntity(hit)) {
						this.cancel()
						return
					}
				}

				// Though we've already done the raycast, go and tick at density locations along this step blocks
				val locStep = position.clone()
				for (i in 1..(step * density).toInt()) {
					// and stop if it returns true
					if (onLocationTick(locStep)) {
						this.cancel()
						return
					}

					locStep.add(origin.direction.clone().multiply(1/density.toFloat()))
				}

				// now move the raytrace position forward in preparation for the next tick
				position.add(origin.direction.clone().multiply(step))

				if (onServerTick(position)) {
					this.cancel()
					return
				}

				count++ // enforce max range so it doesnt go forever
				if (count >= time) {
					this.cancel()
				}
			}
		}

		Runnable().runTaskTimer(Hydrazine.plugin, 0, 1)
	}
}
