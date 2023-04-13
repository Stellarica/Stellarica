package net.stellarica.server.util.extension

import net.stellarica.server.StellaricaServer.Companion.pilotedCrafts
import net.stellarica.server.craft.Craft
import net.stellarica.server.craft.starship.Starship
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * The craft the player is in.
 * @see Player.isPilotingCraft
 */
val Player.craft: Craft?
	get() {
		for (ship in pilotedCrafts) {
			if (ship.passengers.contains(this)) return ship
		}
		return null
	}

/**
 * Whether the player is currently piloting a craft
 *
 * If you couldn't tell by the name, you have an issue.
 * Go get help.
 *
 * @see Player.craft
 */
val Player.isPilotingCraft: Boolean
	get() = (this.craft as? Starship?)?.pilot == this

/**
 * The player's current hotbar
 */
var Player.hotbar: MutableList<ItemStack?>
	get() = MutableList(9) { index -> this.inventory.getItem(index) }
	set(value) {
		for (i in 0..8) this.inventory.setItem(i, value[i])
	}
