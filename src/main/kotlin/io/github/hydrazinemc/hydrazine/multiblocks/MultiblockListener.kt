package io.github.hydrazinemc.hydrazine.multiblocks

import com.destroystokyo.paper.event.server.ServerTickStartEvent
import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.klogger
import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.plugin
import io.github.hydrazinemc.hydrazine.events.HydrazineConfigReloadEvent
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

/**
 * Handles multiblock detection and ticking
 */
class MultiblockListener : Listener {
	companion object {
		/**
		 * All valid [MultiblockLayout]'s loaded from the config
		 */
		var multiblocks = setOf<MultiblockLayout>()
			private set
	}

	private fun validate(rotation: Byte, layout: MultiblockLayout, origin: Block): Boolean {
		fun rotationFunction(it: MultiblockOriginRelative) = when (rotation) {
			1.toByte() -> {
				it
			}

			2.toByte() -> {
				MultiblockOriginRelative(-it.z, it.y, it.x)
			}

			3.toByte() -> {
				MultiblockOriginRelative(-it.x, it.y, -it.z)
			}

			4.toByte() -> {
				MultiblockOriginRelative(it.z, it.y, -it.x)
			}

			else -> throw IllegalArgumentException("Invalid rotation: $rotation")
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
		val potentialMultiblocks = mutableMapOf<MultiblockLayout, Byte>()

		// Start by iterating over each multiblock
		multiblocks.forEach { multiblockConfiguration ->
			if (validate(1, multiblockConfiguration, clickedBlock)) potentialMultiblocks[multiblockConfiguration] = 1
			if (validate(2, multiblockConfiguration, clickedBlock)) potentialMultiblocks[multiblockConfiguration] = 2
			if (validate(3, multiblockConfiguration, clickedBlock)) potentialMultiblocks[multiblockConfiguration] = 3
			if (validate(4, multiblockConfiguration, clickedBlock)) potentialMultiblocks[multiblockConfiguration] = 4
		}

		// If a smaller multiblock exists as part of a larger multiblock, we may end up detecting the wrong one.
		// We use the amount of blocks as a tiebreaker.
		val multiblock = potentialMultiblocks.maxByOrNull { it.key.blocks.size }

		if (multiblock == null) {
			event.player.sendRichMessage("<red>Multiblock is invalid.")
			return
		}

		event.player.sendRichMessage("<green>Found Multiblock: ${multiblock.key.name}")

		val multiblockNamespacedKey = NamespacedKey(plugin, "multiblocks")

		// Get the multiblock list, or create it if it doesn't exist
		val multiblockArray =
			clickedBlock.chunk.persistentDataContainer.get(multiblockNamespacedKey, MultiblockPDC()) ?: mutableSetOf()

		// Create Multiblock
		val multiblockData =
			Multiblock(multiblock.key.name, clickedBlock.x, clickedBlock.y, clickedBlock.z, multiblock.value)

		// Check if the multiblock is already in the list
		if (multiblockArray.contains(multiblockData)) {
			event.player.sendRichMessage("<gold>Multiblock is already detected.")
			return
		}

		// Add the multiblock to the list
		multiblockArray.add(multiblockData)

		// Save it
		clickedBlock.chunk.persistentDataContainer.set(multiblockNamespacedKey, MultiblockPDC(), multiblockArray)
	}

	/**
	 * Confirm multiblocks exist and are intact
	 */
	@EventHandler
	fun onChunkTick(event: ServerTickStartEvent) {
		Bukkit.getWorlds().forEach { world ->
			world.loadedChunks.forEach chunk@{ chunk ->
				// Get the multiblock list of a chunk
				val multiblockArray = chunk.persistentDataContainer.get(
					NamespacedKey(plugin, "multiblocks"),
					MultiblockPDC()
				) ?: return@chunk

				// Iterate over each multiblock
				multiblockArray.forEach multiblock@{ multiblock ->
					// Get the layout
					val multiblockLayout = multiblocks.find { it.name == multiblock.name }

					// If the layout does not exist, undetect the multiblock
					if (multiblockLayout == null) {
						klogger.warn {
							"Chunk ${chunk.x}, ${chunk.z} contains a non-existent multiblock: " +
									"${multiblock.name}, it has been undetected."
						}

						multiblockArray.remove(multiblock)
						chunk.persistentDataContainer.set(
							NamespacedKey(plugin, "multiblocks"),
							MultiblockPDC(),
							multiblockArray
						)

						return@multiblock
					}

					// Validate the layout
					if (!validate(
							multiblock.r,
							multiblockLayout,
							world.getBlockAt(multiblock.x, multiblock.y, multiblock.z)
						)
					) {
						klogger.warn {
							"Chunk ${chunk.x}, ${chunk.z} contains an invalid multiblock: " +
									"${multiblock.name}, it has been undetected."
						}

						multiblockArray.remove(multiblock)
						chunk.persistentDataContainer.set(
							NamespacedKey(plugin, "multiblocks"),
							MultiblockPDC(),
							multiblockArray
						)

						return@multiblock
					}
				}
			}
		}
	}

	/**
	 * Reload all [MultiblockLayout]s from the config file
	 */
	@EventHandler
	fun onConfigReload(event: HydrazineConfigReloadEvent) {
		val newMultiblocks = mutableSetOf<MultiblockLayout>()
		plugin.config.getConfigurationSection("multiblocks")?.getKeys(false)?.forEach multiblockLoop@{ multiblock ->
			// The first thing that needs to be done is we need to get all the keys for the multiblock
			// This way we know what blocks are in the multiblock
			val keys = mutableMapOf<Char, String>()

			plugin.config.getConfigurationSection("multiblocks.$multiblock.key")!!.getKeys(false).forEach { c ->
				keys[c.first()] = plugin.config.getString("multiblocks.$multiblock.key.$c")!!.lowercase()
			}

			val interfaceKey = plugin.config.getString("multiblocks.$multiblock.interface")!!.first()
			if (keys.keys.filter { it == interfaceKey }.count() > 1) {
				klogger.error { "Multiblock $multiblock has multiple interface blocks!" }
			}

			// Now we need to find the interface as all blocks in a multtiblock are stored relative to this point.
			val layers = plugin.config.getConfigurationSection("multiblocks.$multiblock.layers")!!.getKeys(false)

			var interfaceY: Int? = null
			var interfaceZ: Int? = null
			var interfaceX: Int? = null

			// Find the interface
			run layerLoop@{
				layers.forEachIndexed { y, yName ->
					plugin.config.getStringList("multiblocks.$multiblock.layers.$yName").forEachIndexed { z, zString ->
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

			// Create a layout
			val multiblockLayout = MultiblockLayout(multiblock)

			// Now we need to get all the blocks relative to the origin (interface)
			layers.forEachIndexed { y, yName ->
				plugin.config.getStringList("multiblocks.$multiblock.layers.$yName").forEachIndexed { z, zString ->
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
						multiblockLayout.blocks[location] = material!!
					}
				}
			}

			newMultiblocks.add(multiblockLayout)
		}

		multiblocks = newMultiblocks
	}
}
