package net.stellarica.server.crafts.pilotables.starships

import net.minecraft.core.BlockPos
import net.stellarica.server.StellaricaServer.Companion.pilotedCrafts
import net.stellarica.server.StellaricaServer.Companion.plugin
import net.stellarica.server.crafts.Craft
import net.stellarica.server.crafts.pilotables.starships.control.ShipControlHotbar
import net.stellarica.server.crafts.pilotables.starships.subsystems.Subsystem
import net.stellarica.server.crafts.pilotables.starships.subsystems.armor.ArmorSubsystem
import net.stellarica.server.crafts.pilotables.starships.subsystems.shields.ShieldSubsystem
import net.stellarica.server.crafts.pilotables.starships.subsystems.weapons.WeaponSubsystem
import net.stellarica.server.utils.Tasks
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.scheduler.BukkitTask
import kotlin.math.roundToInt

/**
 * Base Starship class
 */
class Starship(origin: BlockPos) : Craft(origin), Listener {

	val subsystems: Set<Subsystem>
		get() = setOf(weapons, shields, armor)


	val weapons = WeaponSubsystem(this)
	val shields = ShieldSubsystem(this)
	val armor = ArmorSubsystem(this)

	/**
	 * The owner of this craft
	 */
	var owner: Player? = null

	/**
	 * The player who is currently piloting this craft
	 */
	var pilot: Player? = null
		private set

	/**
	 * The queued actions from the pilot.
	 * Exists so that movement inputs (such as turning) can be queued, so they are not ignored if the ship is moving.
	 *
	 * Not recommended for actions that can be done immediately (regardless of whether the ship is moving)
	 * as they will do nothing but delay the queue.
	 *
	 * Each tick that the ship is not moving, will execute queued actions until the ship is moving
	 */
	var controlQueue = mutableListOf<() -> Unit>()

	lateinit var movementTask: BukkitTask

	/**
	 * Activates the craft and registers it in [pilotedCrafts]
	 * @param pilot the pilot, who controls the ship
	 * @throws AlreadyPilotedException if the ship is already piloted
	 * @return whether the craft was successfully activated
	 */
	fun activateCraft(pilot: Player): Boolean {
			// Determine passengers, pilot
			if (this.pilot != null) return false
			if (this.blockCount == 0) {
				pilot.sendRichMessage("<red>Cannot pilot empty craft, detect it first!")
				return false
			}
			if (!contains(pilot.location)) {
				pilot.sendRichMessage("<red>You must be inside the craft to pilot it!")
				return false
			}
			passengers.add(pilot)
			this.pilot = pilot
			Bukkit.getOnlinePlayers().filter { it.world == origin.world }.forEach {
				if (contains(it.location) && it != pilot) {
					passengers.add(it)
					it.sendRichMessage("<gray>Now riding a craft piloted by ${pilot.name}!")
				}
			}


		plugin.server.pluginManager.registerEvents(this, plugin)
		ShipControlHotbar.openMenu(pilot)
		StarshipHUD.open(this)
		subsystems.forEach { it.onShipPiloted() }
		movementTask = Tasks.syncRepeat(1, 1) {
			move()
		}

		pilotedCrafts.add(this)
		updateUndetectables()
		messagePilot("<green>Piloted craft!")

		return true
	}

	fun deactivateCraft(): Boolean {
		if (isMoving) {
			messagePilot("<red>Cannot unpilot a moving craft!")
			return false// maybe throw something?
		}
		messagePilot("<green>Unpiloting craft")
		val pass = passengers.toMutableSet()
		subsystems.forEach { it.onShipUnpiloted() }
		movementTask.cancel()
		// close the ship HUD. this is really dumb
		// todo: fix
		this.passengers = pass
		StarshipHUD.close(this)
		this.passengers.clear()
		// close the hotbar menu
		ShipControlHotbar.closeMenu(pilot)

		// todo: maybe find a better way than spamming this for everything we register to
		// reflection?
		BlockExplodeEvent.getHandlerList().unregister(this)
		MultiblockUndetectEvent.handlerList.unregister(this)

		pilot = null
		passengers.clear()
		pilotedCrafts.remove(this)
		return true
	}

	@EventHandler
	fun onBlockExplode(event: BlockExplodeEvent) {
		// todo: fix bad range check
		if (event.block.location.distanceSquared(origin.asLocation) < 500 && contains(event.block.location)) {
			if (shields.shieldHealth > 0) {
				event.isCancelled = true
				shields.damage(event.block.location, event.yield.roundToInt())
			} else {
				detectedBlocks.remove(BlockPos(event.block.location))
			}
		}
	}
}
