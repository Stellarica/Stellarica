package io.github.petercrawley.minecraftstarshipplugin.starships

import com.destroystokyo.paper.event.server.ServerTickStartEvent
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.getPlugin
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.mainConfig
import io.github.petercrawley.minecraftstarshipplugin.customblocks.MSPMaterial
import org.bukkit.Bukkit
import org.bukkit.ChunkSnapshot
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.scheduler.BukkitRunnable
import java.util.concurrent.ConcurrentHashMap

object StarshipManager: BukkitRunnable() {
	private val activeStarships = mutableSetOf<Starship>()
	private val starshipMoveOrders = ConcurrentHashMap<Starship, MutableMap<MSPBlockLocation, BlockData>>()

	private var currentStarship: Starship? = null
	private var currentStarshipMoves: MutableMap<MSPBlockLocation, BlockData>? = null

	private var tickStart = 0L

	class TickInfo: Listener {
		@EventHandler
		fun test(event: ServerTickStartEvent) {
			tickStart = System.currentTimeMillis()
		}
	}

	init {
		this.runTaskTimer(getPlugin(), 1, 1)

		Bukkit.getPluginManager().registerEvents(TickInfo(), getPlugin())
	}

    override fun run() {
        val targetTime = tickStart + 45

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

	fun activateStarship(starship: Starship) {
		activeStarships.add(starship)
	}

	// TODO: If we ever need more speed... https://en.wikipedia.org/wiki/Flood_fill#Span_Filling
	fun detectStarship(starship: Starship) {
		starship.pilot?.sendMessage("Detecting Starship.")

		Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), Runnable {
			val chunkStorage = mutableMapOf<MSPChunkLocation, ChunkSnapshot>()

			val checkedBlocks = mutableSetOf<MSPBlockLocation>() // List of blocks we have checked
			val blocksToCheck = mutableSetOf<MSPBlockLocation>() // List of blocks we need to check

			blocksToCheck.addAll(starship.detectedBlocks) // We need to check that all the blocks we already know about

			// Construct the undetectable list
			val undetectables = mutableSetOf<MSPMaterial>()
			undetectables.addAll(MinecraftStarshipPlugin.forcedUndetectable)  // Add all forced undetectables
			undetectables.addAll(MinecraftStarshipPlugin.defaultUndetectable) // Add all default undetectables
			undetectables.removeAll(starship.allowedBlocks)                   // Remove all that have been allowed by the user

			// Get the detection limit from the config file.
			val detectionLimit = mainConfig.getInt("detectionLimit", 500000)

			while (blocksToCheck.isNotEmpty()) {
				if (starship.detectedBlocks.size > detectionLimit) {
					starship.pilot?.sendMessage("Reached arbitrary detection limit. ($detectionLimit)")
					return@Runnable
				}

				// Get and remove the first item
				val currentBlock = blocksToCheck.first()
				blocksToCheck.remove(currentBlock)

				val chunkCoord = MSPChunkLocation(currentBlock)

				val chunk = chunkStorage.getOrPut(chunkCoord) {
					currentBlock.world.getChunkAt(chunkCoord.x, chunkCoord.z).chunkSnapshot // FIXME: Potentially not thread-safe
				}

				val type = MSPMaterial(chunk.getBlockData(currentBlock.x - (chunkCoord.x shl 4), currentBlock.y, currentBlock.z - (chunkCoord.z shl 4)))

				if (undetectables.contains(type)) continue

				starship.detectedBlocks.add(currentBlock)

				// FIXME: This method of making a set and then iterating over it is probably slower then just 6 if statements.
				// List of neighbouring blocks.
				mutableSetOf(
						currentBlock.relative( 1, 0, 0),
						currentBlock.relative(-1, 0, 0),
						currentBlock.relative( 0, 1, 0),
						currentBlock.relative( 0,-1, 0),
						currentBlock.relative( 0, 0, 1),
						currentBlock.relative( 0, 0,-1)

						// If it's not a block we have checked, check it
				).forEach {
					if (!checkedBlocks.contains(it)) {
						checkedBlocks.add(it)
						blocksToCheck.add(it)
					}
				}
			}

			starship.pilot?.sendMessage("Detected " + starship.detectedBlocks.size + " blocks.") // FIXME: Potentially not thread-safe
		})
	}
}