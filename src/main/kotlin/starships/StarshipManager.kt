package io.github.petercrawley.minecraftstarshipplugin.starships

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.getPlugin
import org.bukkit.block.Block
import org.bukkit.entity.Player

object StarshipManager {
	private val activeStarships = mutableSetOf<Starship>()

	init {
		StarshipTick().runTaskTimer(getPlugin(), 1, 1)
	}

	fun getStarshipAt(block: Block, requester: Player): Starship {
		return Starship(block, requester)
	}

	fun activateStarship(starship: Starship, requester: Player) {
		if (starship.owner == requester) {
			starship.pilot = requester

			activeStarships.add(starship)
		}
	}
}