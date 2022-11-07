package io.github.hydrazinemc.hydrazine.crafts.pilotable.starships

import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.plugin
import io.github.hydrazinemc.hydrazine.crafts.pilotable.Pilotable
import io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.control.ShipControlHotbar
import io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.subsystem.Subsystem
import io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.subsystem.weapons.WeaponSubsystem
import io.github.hydrazinemc.hydrazine.utils.Vector3
import io.github.hydrazinemc.hydrazine.utils.locations.BlockLocation
import org.bukkit.entity.Player

/**
 * Base Starship class
 */
class Starship(origin: BlockLocation) : Pilotable(origin) {
	/**
	 * The velocity (in blocks per second) of this starship
	 * @see maxVelocity
	 */
	var velocity: Vector3 = Vector3.zero
		set(value) {
			field = value.clamp(-maxVelocity, maxVelocity)
		}

	/**
	 * The current acceleration of this starship, in blocks per second per second
	 * @see maxAcceleration
	 */
	var acceleration: Vector3 = Vector3.zero
		set(value) {
			field = value.clamp(-maxAcceleration, maxAcceleration)
		}

	/**
	 * The maximum velocity this starship can travel at
	 * @see velocity
	 */
	var maxVelocity: Vector3 = Vector3(20.0, 20.0, 20.0)

	/**
	 * The maximum acceleration this starship can attain
	 * @see acceleration
	 */
	var maxAcceleration = Vector3(5.0, 5.0, 5.0)

	/**
	 * The number of times per second this ship should move
	 *
	 * Calculated during [move] based on MSPT and time it takes to set blocks
	 */
	var movesPerSecond = 2f
		private set(value) {
			field = value.coerceIn(0.5f, 5f)
		}

	/**
	 * The number of ticks since this ship moved.
	 * (the number of times [move] was called since moving)
	 */
	var ticksSinceMove = 0
		private set



	val subsystems: MutableSet<Subsystem>
		get() = mutableSetOf(weapons)


	val weapons = WeaponSubsystem(this)


	/**
	 * Possibly move the ship depending on [movesPerSecond] and [ticksSinceMove]
	 * Handles [movesPerSecond]
	 *
	 * Called every tick by [StarshipMover], don't call this manually.
	 */
	fun move() {
		ticksSinceMove++
		if (isMoving) return // this might cause issues...
		if (ticksSinceMove >= 20 / movesPerSecond) {
			velocity += acceleration / movesPerSecond
			if (velocity == Vector3.zero) return
			queueMovement((velocity / movesPerSecond).asBlockLocation)

			// it would be better to use the tick time of the tick we moved, but this will work for now
			val weightedTime = plugin.server.averageTickTime + (timeSpentMoving / 2)
			if (weightedTime < 30) movesPerSecond++
			if (weightedTime + timeSpentMoving > 60) movesPerSecond--
			ticksSinceMove = 0
		}
	}

	override fun deactivateCraft(): Boolean {
		val p = pilot // it becomes null after this
		subsystems.forEach { it.onShipUnpiloted() }
		return super.deactivateCraft().also {
			if (it) p?.let { p -> ShipControlHotbar.closeMenu(p) }
		}
	}

	override fun activateCraft(pilot: Player): Boolean {
		return super.activateCraft(pilot).also { result ->
			if (result) {
				ShipControlHotbar.openMenu(pilot)
				subsystems.forEach { it.onShipPiloted() }
			}
		}
	}
}
