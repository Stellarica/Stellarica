package io.github.hydrazinemc.hydrazine.crafts.pilotable.starships

import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.plugin
import io.github.hydrazinemc.hydrazine.crafts.pilotable.Pilotable
import io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.control.ShipControlHotbar
import io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.subsystem.Subsystem
import io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.subsystem.shields.ShieldSubsystem
import io.github.hydrazinemc.hydrazine.crafts.pilotable.starships.subsystem.weapons.WeaponSubsystem
import io.github.hydrazinemc.hydrazine.multiblocks.events.MultiblockUndetectEvent
import io.github.hydrazinemc.hydrazine.utils.Vector3
import io.github.hydrazinemc.hydrazine.utils.locations.BlockLocation
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockExplodeEvent
import kotlin.math.roundToInt

/**
 * Base Starship class
 */
class Starship(origin: BlockLocation) : Pilotable(origin), Listener {
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
		get() = mutableSetOf(weapons, shields, armor)


	val weapons = WeaponSubsystem(this)
	val shields = ShieldSubsystem(this)
	val armor = ArmorSubsystem(this)


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
		val pass = passengers.toMutableSet()
		subsystems.forEach { it.onShipUnpiloted() }
		return super.deactivateCraft().also {
			if (it) {

				// close the ship HUD. this is really dumb
				// todo: fix
				this.passengers = pass
				StarshipHUD.close(this)
				this.passengers.clear()
				// close the hotbar menu
				p?.let { p -> ShipControlHotbar.closeMenu(p) }

				// todo: maybe find a better way than spamming this for everything we register to
				// reflection?
				BlockExplodeEvent.getHandlerList().unregister(this)
				MultiblockUndetectEvent.handlerList.unregister(this)
			}
		}
	}

	override fun activateCraft(pilot: Player): Boolean {
		return super.activateCraft(pilot).also { result ->
			if (result) {
				plugin.server.pluginManager.registerEvents(this, plugin)
				ShipControlHotbar.openMenu(pilot)
				StarshipHUD.open(this)
				subsystems.forEach { it.onShipPiloted() }
			}
		}
	}


	@EventHandler
	fun onBlockExplode(event: BlockExplodeEvent) {
		// todo: fix bad range check
		if (event.block.location.distanceSquared(origin.asLocation) < 500 && contains(event.block.location)) {
			if (shields.shieldHealth > 0) {
				event.isCancelled = true
				shields.damage(event.block.location, event.yield.roundToInt())
			}
			else {
				detectedBlocks.remove(BlockLocation(event.block.location))
			}
		}
	}

	@EventHandler
	fun onMultiblockUndetect(event: MultiblockUndetectEvent) {
		if (multiblocks.contains(event.multiblock)) {
			multiblocks.remove(event.multiblock)
			subsystems.forEach {
				it.onMultiblockUndetected(event.multiblock)
			}
		}
	}
}
