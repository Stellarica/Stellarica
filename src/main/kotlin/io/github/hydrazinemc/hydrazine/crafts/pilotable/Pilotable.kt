package io.github.hydrazinemc.hydrazine.crafts.pilotable

import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.pilotedCrafts
import io.github.hydrazinemc.hydrazine.crafts.Craft
import io.github.hydrazinemc.hydrazine.utils.AlreadyPilotedException
import io.github.hydrazinemc.hydrazine.utils.extensions.sendMiniMessage
import org.bukkit.Location
import org.bukkit.entity.Player

open class Pilotable(origin: Location): Craft(origin) {

	var owner: Player? = null
	var pilot: Player? = null
		private set

	/**
	 * Activates the craft and registers it in [pilotedCrafts]
	 * @param pilot the pilot, who controls the ship
	 * @throws AlreadyPilotedException if the ship is already piloted
	 */
	fun activateCraft(pilot: Player) {
		// Determine passengers, pilot
		if (this.pilot != null) throw AlreadyPilotedException(this, pilot)
		passengers.add(pilot)
		this.pilot = pilot
		pilotedCrafts.add(this)
		updateUndetectables()
		messagePilot("<green>Piloted craft!")
	}

	/**
	 * Deactivate the craft and remove it from [pilotedCrafts]
	 * @return whether the ship successfully deactivated
	 */
	fun deactivateCraft(): Boolean {
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