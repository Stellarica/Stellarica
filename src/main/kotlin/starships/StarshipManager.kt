package io.github.petercrawley.minecraftstarshipplugin.starships

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.getPlugin
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.util.concurrent.ConcurrentHashMap

object StarshipManager: BukkitRunnable() {
	private val activeStarships = mutableSetOf<Starship>()
	private val starshipMoveOrders = ConcurrentHashMap<Starship, MutableMap<MSPBlockLocation, BlockData>>()

	private var currentStarship: Starship? = null
	private var currentStarshipMoves: MutableMap<MSPBlockLocation, BlockData>? = null

	init { this.runTaskTimer(getPlugin(), 1, 1) }

    override fun run() {
        val start = System.currentTimeMillis()

        val targetTime = start + 40

		activeStarships.forEach { starship ->
			// TODO: This can be moved to a separate thread by using a ChunkSnapshot
			// TODO: Collision Detection
			// That is why starshipMoveOrders is a ConcurrentHashMap and not a MutableMap
			if (currentStarship == starship || starshipMoveOrders.contains(starship)) return@forEach // Don't make multiple move orders for a ship.

			val newBlockLocations = mutableSetOf<MSPBlockLocation>()

			val starshipMoves = mutableMapOf<MSPBlockLocation, BlockData>()

			starship.detectedBlocks.forEach { block ->
				starshipMoves[block.clone()] = Bukkit.createBlockData(Material.AIR)
			}

			starship.detectedBlocks.forEach { block ->
				val newLocation = block.relative(1, 0, 0)

				newBlockLocations.add(newLocation)
				starshipMoves[newLocation] = block.bukkit().blockData
			}

			starshipMoveOrders[starship] = starshipMoves
			starship.detectedBlocks = newBlockLocations // Set the new block locations.
		}

	    if (currentStarship == null && starshipMoveOrders.isNotEmpty()) {
			currentStarship = starshipMoveOrders.keys.first()
		    currentStarshipMoves = starshipMoveOrders.remove(currentStarship)
	    }

	    val movesToRemove = mutableSetOf<MSPBlockLocation>()

	    currentStarshipMoves?.forEach { move ->
		    if (System.currentTimeMillis() > targetTime) return@forEach
			move.key.bukkit().setBlockData(move.value, false)
		    movesToRemove.add(move.key)
	    }

	    movesToRemove.forEach { block ->
			currentStarshipMoves?.remove(block)
	    }

	    if (currentStarshipMoves?.isEmpty() == true) {
			currentStarship = null
		    currentStarshipMoves = null
	    }
    }

	fun getStarshipAt(block: Block, requester: Player): Starship { return Starship(block, requester) }

	fun activateStarship(starship: Starship, requester: Player) {
		if (starship.owner == requester) {
			starship.pilot = requester

			activeStarships.add(starship)
		}
	}
}