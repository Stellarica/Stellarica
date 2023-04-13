package net.stellarica.server.projectile

import net.stellarica.server.StellaricaServer.Companion.pilotedCrafts
import net.stellarica.server.craft.Craft
import net.stellarica.server.util.Tasks
import net.stellarica.server.util.extension.toBlockPos
import net.stellarica.server.util.extension.toVec3i
import org.bukkit.FluidCollisionMode
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import org.bukkit.util.RayTraceResult

interface Projectile<T> {
	/** The amount of time the projectile is active. */
	val time: Int

	/** The number of times to call onLocationTick per block travelled per tick */
	val density: Int

	fun shoot(shooter: Craft, origin: Location)
	fun shoot(shooter: LivingEntity, origin: Location)

	/** Called every step * density times per tick, at incremental locations */
	fun onLocationStep(data: T, loc: Location)

	/**
	 * Called every server tick this projectile is running
	 * @return how far to move next tick, zero to stop
	 */
	fun onServerTick(data: T, loc: Location): Double

	fun onHitCraft(data: T, loc: Location, craft: Craft): Boolean

	/**
	 * Called when the projectile hits something
	 * @return whether to continue onward
	 */
	fun onHitBlockOrEntity(data: T, res: RayTraceResult): Boolean

	/**
	 * Handles projectile raycasts
	 */
	fun cast(
		/**
		 * The location and direction to shoot from
		 */
		origin: Location,
		data: T
	) {
		/** the current position */
		val position = origin.clone()

		/** the number of ticks this has been running*/
		var count = 0

		var dist = 0.0

		Tasks.syncRepeat(0, 1) {
			// ray trace as far as it moves this tick (step blocks)
			val hit = position.world.rayTrace(
				position,
				origin.direction,
				dist,
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
				pilotedCrafts.filter { it.world == position.world }
					.filter { it.origin.distSqr(position.toVec3i()) < 1000 }.forEach {
						if (it.contains(hitLoc.toBlockPos())) {
							if (onHitCraft(data, hitLoc, it)) {
								this.cancel()
								return@syncRepeat
							}
						}
					}

				if (onHitBlockOrEntity(data, hit)) {
					this.cancel()
					return@syncRepeat
				}
			}

			// Though we've already done the raycast, go and tick at density locations along this step blocks
			val locStep = position.clone()
			for (i in 1..(dist * density).toInt()) {
				onLocationStep(data, locStep)
				locStep.add(origin.direction.clone().multiply(1 / density.toFloat()))
			}

			// now move the raytrace position forward in preparation for the next tick
			position.add(origin.direction.clone().multiply(dist))

			// figure out how far to go next tick
			dist = onServerTick(data, position)
			if (dist <= 0) {
				this.cancel()
				return@syncRepeat
			}

			count++ // enforce max range so it doesn't go forever
			if (count >= time) {
				this.cancel()
			}
		}
	}
}
