package io.github.petercrawley.minecraftstarshipplugin.starships

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.defaultUndetectable
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.forcedUndetectable
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.getPlugin
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.mainConfig
import io.github.petercrawley.minecraftstarshipplugin.customblocks.MSPMaterial
import io.github.petercrawley.minecraftstarshipplugin.starships.StarshipManager.blockSetQueue
import io.github.petercrawley.minecraftstarshipplugin.starships.StarshipManager.playerTeleportQueue
import org.bukkit.Bukkit
import org.bukkit.ChunkSnapshot
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player

class Starship(origin: Block, private val pilot: Player) {
	val owner = pilot

	private val detectedBlocks = mutableSetOf(MSPBlockLocation(origin)) // Blocks that we know are part of the ship.

	var allowedBlocks = setOf<MSPMaterial>() // Blocks that have been specifically allowed.

	fun detect() {
		Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), Runnable {
			val startTime = System.currentTimeMillis() // Debug

			pilot.sendMessage("Detecting Starship.")

			class ChunkCoord(val x: Int, val z: Int) {
				constructor(block: MSPBlockLocation): this(block.x shr 4, block.z shr 4)

				override fun equals(other: Any?): Boolean {
					if (this === other) return true
					if (javaClass != other?.javaClass) return false

					other as ChunkCoord

					if (x != other.x) return false
					if (z != other.z) return false

					return true
				}

				override fun hashCode(): Int {
					return 31 * (x) + z
				}
			}

			val chunkStorage = mutableMapOf<ChunkCoord, ChunkSnapshot>()

			val checkedBlocks = mutableSetOf<MSPBlockLocation>() // List of blocks we have checked
			val blocksToCheck = mutableSetOf<MSPBlockLocation>() // List of blocks we need to check

			blocksToCheck.addAll(detectedBlocks) // We need to check that all the blocks we already know about

			// Construct the undetectable list
			val undetectables = mutableSetOf<MSPMaterial>()
			undetectables.addAll(forcedUndetectable)
			undetectables.addAll(defaultUndetectable)
			undetectables.removeAll(allowedBlocks)

			// Get the detection limit from the config file.
			val detectionLimit = mainConfig.getInt("detectionLimit", 500000)

			while (blocksToCheck.isNotEmpty()) {
				if (detectedBlocks.size > detectionLimit) {
					pilot.sendMessage("Reached arbitrary detection limit. ($detectionLimit)")

					// Debug
					val endTime = System.currentTimeMillis()
					if (mainConfig.getBoolean("timeOperations", false)) {
						getPlugin().logger.info("Ship detection took: " + (endTime - startTime) + "ms.")
						pilot.sendMessage("Ship detection took: " + (endTime - startTime) + "ms.")
					}

					return@Runnable
				}

				// Get and remove the first item
				val currentBlock = blocksToCheck.first()
				blocksToCheck.remove(currentBlock)

				val chunkCoord = ChunkCoord(currentBlock)

				val chunk = chunkStorage.getOrPut(chunkCoord) {
					currentBlock.world.getChunkAt(chunkCoord.x, chunkCoord.z).chunkSnapshot
				}

				val type = MSPMaterial(chunk.getBlockData(currentBlock.x - (chunkCoord.x shl 4), currentBlock.y, currentBlock.z - (chunkCoord.z shl 4)))

				if (undetectables.contains(type)) continue

				detectedBlocks.add(currentBlock)

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

			pilot.sendMessage("Detected " + detectedBlocks.size + " blocks.")

			// Debug
			val endTime = System.currentTimeMillis()
			if (mainConfig.getBoolean("timeOperations", false)) {
				getPlugin().logger.info("Ship detection took: " + (endTime - startTime) + "ms.")
				pilot.sendMessage("Ship detection took: " + (endTime - startTime) + "ms.")
			}
		})
	}

	private fun move(x: Int, y: Int, z: Int) {
		Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), Runnable {
			val startTime = System.currentTimeMillis() // Debug

			detectedBlocks.forEach {
				val targetBlock = it.relative(x, y, z)

				if (!detectedBlocks.contains(targetBlock)) {
					if (!targetBlock.bukkit().type.isAir) {
						pilot.sendMessage("Obstructed at " + targetBlock.x + ", " + targetBlock.y + ", " + targetBlock.z  + " by " + targetBlock.bukkit().type.toString())
						return@Runnable
					}
				}
			}

			val blocksToUpdate: MutableMap<Block, BlockData> = mutableMapOf()

			val airBlockData = Bukkit.getServer().createBlockData(Material.AIR)

			detectedBlocks.forEach {
				blocksToUpdate[it.bukkit()] = airBlockData
				blocksToUpdate[it.relative(x, y, z).bukkit()] = it.bukkit().blockData
			}

			// Remember we have not actually made any changes yet, but we now have a list of all the positions, and what they need to be changed too.
			blocksToUpdate.forEach{
				blockSetQueue[it.key] = it.value
			}

			playerTeleportQueue[pilot] =  pilot.location.add(x.toDouble(), y.toDouble(), z.toDouble())

			val endTime = System.currentTimeMillis()

			if (mainConfig.getBoolean("timeOperations", false)) {
				getPlugin().logger.info("Ship movement took: " + (endTime - startTime) + "ms.")
			}
		})
	}
}