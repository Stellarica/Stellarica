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

        val targetTime = start + 50

		activeStarships.forEach { starship ->
			// TODO: This can be moved to a separate thread by using a ChunkSnapshot
			// That is why starshipMoveOrders is a ConcurrentHashMap and not a MutableMap
			val starshipMoves = mutableMapOf<MSPBlockLocation, BlockData>()

			starship.detectedBlocks.forEach { block ->
				starshipMoves.putIfAbsent(block.clone(), Bukkit.createBlockData(Material.AIR))

				val newLocation = block.add(1, 0, 0)
				val newBlockData = block.bukkit().blockData

				if (newLocation.bukkit().blockData != newBlockData) starshipMoves[newLocation] = newBlockData
			}

			starshipMoveOrders[starship] = starshipMoves
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