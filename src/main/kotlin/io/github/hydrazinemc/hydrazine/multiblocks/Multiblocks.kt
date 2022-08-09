package io.github.hydrazinemc.hydrazine.multiblocks

import com.destroystokyo.paper.event.server.ServerTickStartEvent
import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.klogger
import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.plugin
import io.github.hydrazinemc.hydrazine.events.HydrazineConfigReloadEvent
import io.github.hydrazinemc.hydrazine.utils.extensions.asMiniMessage
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.persistence.PersistentDataType
import java.util.UUID

/**
 * Handles multiblock detection and ticking
 */
class Multiblocks : Listener {
	companion object {
		/**
		 * All valid [MultiblockType]'s loaded from the config
		 */
		var types = setOf<MultiblockType>()
			private set
	}

	private fun validate(facing: BlockFace, layout: MultiblockType, origin: Block): Boolean {
		fun rotationFunction(it: MultiblockOriginRelative) = when (facing) {
			BlockFace.NORTH -> it
			BlockFace.EAST -> MultiblockOriginRelative(-it.z, it.y, it.x)
			BlockFace.SOUTH -> MultiblockOriginRelative(-it.x, it.y, -it.z)
			BlockFace.WEST -> MultiblockOriginRelative(it.z, it.y, -it.x)

			else -> throw IllegalArgumentException("Invalid facing direction: $facing")
		}

		layout.blocks.forEach {
			val rotatedLocation = rotationFunction(it.key)

			val relativeBlock = getId(origin.getRelative(rotatedLocation.x, rotatedLocation.y, rotatedLocation.z))

			if (relativeBlock != it.value) return false // A block we were expecting is missing, so break the function.
		}

		return true // Valid, save it and keep looking.
	}

	/**
	 * Handles detection of multiblocks
	 */
	@EventHandler
	fun onMultiblockDetection(event: PlayerInteractEvent) {
		if (event.action != Action.RIGHT_CLICK_BLOCK ||
			event.hand != EquipmentSlot.HAND ||
			event.player.isSneaking
		) return

		val clickedBlock = event.clickedBlock!!
		if (getId(clickedBlock) != "interface_block") return

		event.isCancelled = true

		// Actually detect it
		val potentialMultiblocks = mutableMapOf<MultiblockType, BlockFace>()

		// Start by iterating over each multiblock
		types.forEach { multiblockConfiguration ->
			setOf(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST).forEach { facing ->
				if (validate(facing, multiblockConfiguration, clickedBlock)) {
					potentialMultiblocks[multiblockConfiguration] = facing
				}
			}
		}

		// If a smaller multiblock exists as part of a larger multiblock, we may end up detecting the wrong one.
		// We use the amount of blocks as a tiebreaker.
		val multiblock = potentialMultiblocks.maxByOrNull { it.key.blocks.size }

		if (multiblock == null) {
			event.player.sendRichMessage("<red>Multiblock is invalid.")
			return
		}

		event.player.sendRichMessage("<green>Found Multiblock: ${multiblock.key.name}")

		// Get the multiblock list
		val multiblockArray = clickedBlock.chunk.multiblocks

		// Create Multiblock
		val multiblockData =
			MultiblockInstance(
				types.first {multiblock.key.name == it.name},
				UUID.randomUUID(),
				clickedBlock.location,
				multiblock.value,
				clickedBlock.chunk.persistentDataContainer.adapterContext.newPersistentDataContainer()
			)

		// Check if the multiblock is already in the list
		multiblockArray.forEach { existing ->
			// Cant do equality check because the UUID will be different
			if (
				multiblockData.type == existing.type &&
				multiblockData.origin == existing.origin &&
				multiblockData.facing == existing.facing
			) {
				event.player.sendRichMessage("<gold>Multiblock is already detected.")
				return
			}
		}

		// Add the multiblock to the list if it wasn't already detected
		multiblockArray.add(multiblockData)

		// Save it
		clickedBlock.chunk.multiblocks = multiblockArray
	}

	/**
	 * Confirm multiblocks exist and are intact
	 */
	@EventHandler
	fun onChunkTick(event: ServerTickStartEvent) {
		Bukkit.getWorlds().forEach { world ->
			world.loadedChunks.forEach chunk@{ chunk ->
				// Get the multiblock list of a chunk
				val multiblockArray = chunk.multiblocks
				val newMultiblockArray = mutableSetOf<MultiblockInstance>()
				// Iterate over each multiblock
				multiblockArray.forEach multiblock@{ multiblock ->
					// Get the layout
					val multiblockLayout = types.find { it.name == multiblock.type.name }

					// If the layout does not exist, undetect the multiblock
					if (multiblockLayout == null) {
						klogger.warn {
							"Chunk ${chunk.x}, ${chunk.z} contains a non-existent multiblock type: " +
									"${multiblock.type.name}, it has been undetected."
						}
						return@multiblock
					}

					// Validate the layout
					if (!validate(
							multiblock.facing,
							multiblockLayout,
							world.getBlockAt(multiblock.origin)
						)
					) {
						klogger.warn {
							"Chunk ${chunk.x}, ${chunk.z} contains an invalid multiblock: " +
									"${multiblock.type.name} (${multiblock.uuid}), it has been undetected."
						}
						return@multiblock
					} else {
						newMultiblockArray.add(multiblock)
						multiblock.type.onTick(multiblock) // tick the multiblock
					}
				}
				chunk.multiblocks = newMultiblockArray
			}
		}
	}

	/**
	 * Reload all [MultiblockType]s from the config file
	 */
	@EventHandler
	fun onConfigReload(event: HydrazineConfigReloadEvent) {
		val newMultiblocks = mutableSetOf<MultiblockType>()
		plugin.config.getConfigurationSection("multiblocks")?.getKeys(false)?.forEach multiblockLoop@{ multiblockName ->
			// The first thing that needs to be done is we need to get all the keys for the multiblock
			// This way we know what blocks are in the multiblock
			val keys = mutableMapOf<Char, String>()

			plugin.config.getConfigurationSection("multiblocks.$multiblockName.key")!!.getKeys(false).forEach { c ->
				keys[c.first()] = plugin.config.getString("multiblocks.$multiblockName.key.$c")!!.lowercase()
			}

			val interfaceKey = plugin.config.getString("multiblocks.$multiblockName.interface")!!.first()
			if (keys.keys.filter { it == interfaceKey }.count() > 1) {
				klogger.error { "Multiblock type $multiblockName has multiple interface blocks!" }
			}

			// Now we need to find the interface as all blocks in a multtiblock are stored relative to this point.
			val layers = plugin.config.getConfigurationSection("multiblocks.$multiblockName.layers")!!.getKeys(false)

			var interfaceY: Int? = null
			var interfaceZ: Int? = null
			var interfaceX: Int? = null

			// Find the interface
			run layerLoop@{
				layers.forEachIndexed { y, yName ->
					plugin.config.getStringList("multiblocks.$multiblockName.layers.$yName").forEachIndexed { z, zString ->
						zString.forEachIndexed { x, xChar ->
							if (xChar == interfaceKey) {
								interfaceY = y
								interfaceZ = z
								interfaceX = x

								return@layerLoop
							}
						}
					}
				}
			}

			// Now we need to get all the blocks relative to the origin (interface)
			val blocks = mutableMapOf<MultiblockOriginRelative, String>()
			layers.forEachIndexed { y, yName ->
				plugin.config.getStringList("multiblocks.$multiblockName.layers.$yName").forEachIndexed { z, zString ->
					zString.forEachIndexed { x, xChar ->
						// Find relative position
						val relativeY = y - interfaceY!!
						val relativeZ = z - interfaceZ!!
						val relativeX = x - interfaceX!!

						// Get the material from keys
						val material = keys[xChar]

						// Construct a MultiblockOriginRelativeLocation
						val location = MultiblockOriginRelative(relativeX, relativeY, relativeZ)

						// Add the block to the multiblock configuration
						blocks[location] = material!!
					}
				}
			}
			newMultiblocks.add(MultiblockType(
					multiblockName,
					blocks
			) { instance ->
				val times = instance.data?.getOrDefault(NamespacedKey(plugin, "timesticked"), PersistentDataType.INTEGER, 0) ?: 0
				if (plugin.server.currentTick % 40 == 0) plugin.server.broadcast("${instance.uuid} has ticked $times times!".asMiniMessage)
				instance.data?.set(NamespacedKey(plugin, "timesticked"), PersistentDataType.INTEGER, times + 1)
			})
		}
		types = newMultiblocks
	}
}
