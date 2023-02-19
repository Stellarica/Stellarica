package net.stellarica.server.crafts.pilotables.starships

import net.stellarica.server.StellaricaServer.Companion.plugin
import net.stellarica.server.crafts.pilotables.Pilotable
import net.stellarica.server.crafts.pilotables.starships.control.ShipControlHotbar
import net.stellarica.server.crafts.pilotables.starships.subsystems.Subsystem
import net.stellarica.server.crafts.pilotables.starships.subsystems.armor.ArmorSubsystem
import net.stellarica.server.crafts.pilotables.starships.subsystems.shields.ShieldSubsystem
import net.stellarica.server.crafts.pilotables.starships.subsystems.weapons.WeaponSubsystem
import net.stellarica.server.multiblocks.events.MultiblockUndetectEvent
import net.stellarica.server.utils.Tasks
import net.stellarica.server.utils.locations.BlockLocation
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.scheduler.BukkitTask
import kotlin.math.roundToInt

/**
 * Base Starship class
 */
class Starship(origin: BlockLocation) : Pilotable(origin), Listener {

	val subsystems: Set<Subsystem>
		get() = setOf(weapons, shields, armor)


	val weapons = WeaponSubsystem(this)
	val shields = ShieldSubsystem(this)
	val armor = ArmorSubsystem(this)

	lateinit var movementTask: BukkitTask

	override fun deactivateCraft(): Boolean {
		val p = pilot // it becomes null after this
		val pass = passengers.toMutableSet()
		subsystems.forEach { it.onShipUnpiloted() }
		return super.deactivateCraft().also {
			if (it) {
				movementTask.cancel()
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
				movementTask = Tasks.syncRepeat(1, 1) {
					move()
				}
			}
		}
	}

	fun move() {

	}

	@EventHandler
	fun onBlockExplode(event: BlockExplodeEvent) {
		// todo: fix bad range check
		if (event.block.location.distanceSquared(origin.asLocation) < 500 && contains(event.block.location)) {
			if (shields.shieldHealth > 0) {
				event.isCancelled = true
				shields.damage(event.block.location, event.yield.roundToInt())
			} else {
				detectedBlocks.remove(BlockLocation(event.block.location))
			}
		}
	}
}
