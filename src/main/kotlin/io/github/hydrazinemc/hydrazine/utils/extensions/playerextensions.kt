package io.github.hydrazinemc.hydrazine.utils.extensions

import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.activeStarships
import io.github.hydrazinemc.hydrazine.starships.Starship
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * The starship the player is riding
 * @see Player.isPilotingShip
 */
val Player.starship: Starship?
	get() {
		activeStarships.forEach{ship ->
			if (ship.passengers.contains(this)) return ship
		}
		return null
	}

/**
 * Whether the player is currently piloting a starship.
 *
 * If you couldn't tell by the name, you have an issue.
 * Go get help.
 *
 * @see Player.starship
 */
val Player.isPilotingShip: Boolean
	get() = this.starship?.pilot == this

fun Player.setHotbar(bar: MutableList<ItemStack?>) {
	for (i in 0..9) {
		this.inventory.setItem(i, bar[i])
	}
}

val Player.hotbar: MutableList<ItemStack?>
	get() {
		// this could be code golfed
		val bar = mutableListOf<ItemStack?>()
		for (i in 0..9) {
			bar[i] = this.inventory.getItem(i)
		}
		return bar
	}