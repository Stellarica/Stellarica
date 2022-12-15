package io.github.hydrazinemc.hydrazine.server.utils.extensions

import io.github.hydrazinemc.hydrazine.server.HydrazineServer.Companion.pilotedCrafts
import io.github.hydrazinemc.hydrazine.server.crafts.Craft
import io.github.hydrazinemc.hydrazine.server.crafts.pilotable.Pilotable
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * The craft the player is in.
 * @see Player.isPilotingCraft
 */
val Player.craft: Craft?
	get() {
		pilotedCrafts.forEach { ship ->
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
	get() = (this.craft as? Pilotable?)?.pilot == this

/**
 * The player's current hotbar
 */
var Player.hotbar: MutableList<ItemStack?>
	get() = MutableList(9) { index -> this.inventory.getItem(index) }
	set(value) {
		for (i in 0..8) this.inventory.setItem(i, value[i])
	}
