package io.github.hydrazinemc.hydrazine.server.crafts.pilotables

import io.github.hydrazinemc.hydrazine.server.HydrazineServer.Companion.pilotedCrafts
import io.github.hydrazinemc.hydrazine.server.crafts.Craft
import io.github.hydrazinemc.hydrazine.server.utils.AlreadyPilotedException
import io.github.hydrazinemc.hydrazine.server.utils.locations.BlockLocation
import org.bukkit.Bukkit
import org.bukkit.entity.Player

/**
 * Base class for all pilotables Crafts.
 */
open class Pilotable(origin: BlockLocation) : Craft(origin) {

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

	/**
	 * Activates the craft and registers it in [pilotedCrafts]
	 * @param pilot the pilot, who controls the ship
	 * @throws AlreadyPilotedException if the ship is already piloted
	 * @return whether the craft was successfully activated
	 */
	open fun activateCraft(pilot: Player): Boolean {
		// Determine passengers, pilot
		if (this.pilot != null) throw AlreadyPilotedException(this, pilot)
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
		pilotedCrafts.add(this)
		updateUndetectables()
		messagePilot("<green>Piloted craft!")
		return true
	}

	/**
	 * Deactivate the craft and remove it from [pilotedCrafts]
	 * @return whether the ship successfully deactivated
	 */
	open fun deactivateCraft(): Boolean {
		if (isMoving) {
			messagePilot("<red>Cannot unpilot a moving craft!")
			return false// maybe throw something?
		}
		messagePilot("<green>Unpiloting craft")
		pilot = null
		passengers.clear()
		pilotedCrafts.remove(this)
		return true
	}
}
