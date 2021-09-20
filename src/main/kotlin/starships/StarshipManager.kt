package io.github.petercrawley.minecraftstarshipplugin.starships

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.getPlugin
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

object StarshipManager: BukkitRunnable() {
	private val activeStarships = mutableSetOf<Starship>()

	init {
		this.runTaskTimer(getPlugin(), 1, 1)
	}

    override fun run() {
        val start = System.currentTimeMillis()

        val targetTime = start + 50
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