package io.github.hydrazinemc.hydrazine.multiblocks

import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.klogger
import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.plugin
import io.github.hydrazinemc.hydrazine.events.HydrazineConfigReloadEvent
import io.github.hydrazinemc.hydrazine.multiblocks.events.MultiblockDetectEvent
import io.github.hydrazinemc.hydrazine.multiblocks.events.MultiblockLoadEvent
import io.github.hydrazinemc.hydrazine.multiblocks.events.MultiblockUndetectEvent
import io.github.hydrazinemc.hydrazine.multiblocks.events.MultiblockUnloadEvent
import io.github.hydrazinemc.hydrazine.utils.OriginRelative
import io.github.hydrazinemc.hydrazine.utils.Tasks
import io.github.hydrazinemc.hydrazine.utils.extensions.id
import io.github.hydrazinemc.hydrazine.utils.locations.BlockLocation
import org.bukkit.Chunk
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import java.util.UUID

/**
 * Handles multiblock detection and ticking
 */
object Multiblocks : Listener {
	/**
	 * All valid [MultiblockType]'s loaded from the config
	 */
	var types = setOf<MultiblockType>()
		private set

	/**
	 * All currently loaded multiblocks
	 */
	var activeMultiblocks = mutableSetOf<MultiblockInstance>()
		private set

	/**
	 * String "id"s for each type of block that is used as an interface
	 * (the blocks that can be clicked on to detect a multiblock)
	 */
	private val interfaceBlockTypes = mutableSetOf<String>()

	init { // init block on an object? :conc:
		Tasks.syncRepeat(10, 1) {
			val invalid = mutableSetOf<MultiblockInstance>()
			activeMultiblocks.forEach { multiblock ->
				// Validate the layout
				if (!validate(
						multiblock.facing,
						multiblock.type,
						multiblock.origin.block
					)
				) {
					klogger.warn {
						"Invalid multiblock at ${BlockLocation(multiblock.origin).formattedString}: " +
								"${multiblock.type.name} (${multiblock.uuid}), it has been undetected."
					}
					invalid.add(multiblock)
					return@forEach
				}
			}
			invalid.forEach {
				plugin.server.pluginManager.callEvent(MultiblockUndetectEvent(it))
			}
			activeMultiblocks.removeAll(invalid)
		}
	}


	private fun validate(facing: BlockFace, layout: MultiblockType, origin: Block): Boolean {
		fun rotationFunction(it: OriginRelative) = when (facing) { // maybe we can repurpose this for ship movement?
			BlockFace.NORTH -> it
			BlockFace.EAST -> OriginRelative(-it.z, it.y, it.x)
			BlockFace.SOUTH -> OriginRelative(-it.x, it.y, -it.z)
			BlockFace.WEST -> OriginRelative(it.z, it.y, -it.x)

			else -> throw IllegalArgumentException("Invalid facing direction: $facing")
		}

		layout.blocks.forEach {
			val rotatedLocation = rotationFunction(it.key)
			val relativeBlock = origin.getRelative(rotatedLocation.x, rotatedLocation.y, rotatedLocation.z)
			if (relativeBlock.id != it.value) {
				return false
			} // A block we were expecting is missing, so break the function.
		}
		return true // Valid, save it and keep looking.
	}

	/**
	 * Handles detection of multiblocks
	 */
	@EventHandler
	fun onMultiblockDetection(event: PlayerInteractEvent) {
		if (event.action != Action.RIGHT_CLICK_BLOCK || event.player.isSneaking) return
		val clickedBlock = event.clickedBlock!!
		if (clickedBlock.id !in interfaceBlockTypes) return


		val potentialMultiblocks = mutableMapOf<MultiblockType, BlockFace>()

		// Start by iterating over each multiblock
		types.forEach { type ->
			setOf(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST).forEach { facing ->
				if (validate(facing, type, clickedBlock)) {
					potentialMultiblocks[type] = facing
				}
			}
		}

		// If a smaller multiblock exists as part of a larger multiblock, we may end up detecting the wrong one.
		// We use the amount of blocks as a tiebreaker.
		val multiblock = potentialMultiblocks.maxByOrNull { it.key.blocks.size } ?: run {
			event.player.sendRichMessage("<red>Multiblock is invalid.")
			return
		}

		event.player.sendRichMessage("<green>Found Multiblock: ${multiblock.key.name}")
		event.isCancelled = true

		// Create Multiblock
		val multiblockData = MultiblockInstance(
			multiblock.key,
			UUID.randomUUID(),
			clickedBlock.location,
			multiblock.value,
			clickedBlock.chunk.persistentDataContainer.adapterContext.newPersistentDataContainer()
		)

		// Check if the multiblock is already in the list
		// could probably filter this more to reduce the number of checks
		activeMultiblocks.filter { it.type == multiblockData.type }.forEach { existing ->
			// Cant do equality check because the UUID will be different
			if (
				multiblockData.origin == existing.origin &&
				multiblockData.facing == existing.facing
			) {
				event.player.sendRichMessage("<gold>Multiblock is already detected.")
				return
			}
		}

		// Save it
		val detect = MultiblockDetectEvent(multiblockData)
		plugin.server.pluginManager.callEvent(detect)
		if (!detect.isCancelled) activeMultiblocks.add(multiblockData)
	}

	/**
	 * Load multiblocks when the chunk is loaded
	 */
	@EventHandler
	fun onChunkLoad(event: ChunkLoadEvent) {
		// if (event.chunk.savedMultiblocks.isNotEmpty()) println("loaded " + event.chunk.savedMultiblocks)
		val multiblocks = event.chunk.savedMultiblocks
		Tasks.sync {
			multiblocks.forEach {
				plugin.server.pluginManager.callEvent(MultiblockLoadEvent(it))
			}
			activeMultiblocks.addAll(multiblocks)
			event.chunk.savedMultiblocks = setOf()
		}
	}

	/**
	 * Unload multiblocks when the chunk is unloaded
	 */
	@EventHandler
	fun onChunkUnload(event: ChunkUnloadEvent) {
		val chunkMultiblocks = activeMultiblocks.filter { it.origin.chunk == event.chunk }.toSet()
		// this is probably laggy and should be fixed
		// if (chunkMultiblocks.isNotEmpty()) println("saving " + chunkMultiblocks)
		Tasks.sync { // chunks are sometimes handled async
			chunkMultiblocks.forEach {
				plugin.server.pluginManager.callEvent(MultiblockUnloadEvent(it))
			}
			event.chunk.savedMultiblocks = chunkMultiblocks
			activeMultiblocks.removeAll(chunkMultiblocks)
		}
	}

	/**
	 * Reload all [MultiblockType]s from the config file
	 */
	@EventHandler
	fun onConfigReload(event: HydrazineConfigReloadEvent) {
		interfaceBlockTypes.clear()
		val newMultiblocks = mutableSetOf<MultiblockType>()
		plugin.config.getConfigurationSection("multiblocks")?.getKeys(false)?.forEach multiblockLoop@{ multiblockName ->
			// The first thing that needs to be done is we need to get all the keys for the multiblock
			// This way we know what blocks are in the multiblock
			val keys = mutableMapOf<Char, String>()
			plugin.config.getConfigurationSection("multiblocks.$multiblockName.key")!!.getKeys(false).forEach { c ->
				keys[c.first()] = plugin.config.getString("multiblocks.$multiblockName.key.$c")!!.lowercase()
			}

			val interfaceKey = plugin.config.getString("multiblocks.$multiblockName.interface")!!.first()

			// Now we need to find the interface as all blocks in a multtiblock are stored relative to this point.
			val layers = plugin.config.getConfigurationSection("multiblocks.$multiblockName.layers")!!.getKeys(false)

			var interfaceY: Int? = null
			var interfaceZ: Int? = null
			var interfaceX: Int? = null

			// Find the interface
			run layerLoop@{
				layers.forEachIndexed { y, yName ->
					plugin.config.getStringList("multiblocks.$multiblockName.layers.$yName")
						.forEachIndexed { z, zString ->
							zString.forEachIndexed { x, xChar ->
								if (xChar == interfaceKey) {
									interfaceY = y
									interfaceZ = z
									interfaceX = x
									interfaceBlockTypes.add(keys[xChar]!!)
									return@layerLoop
								}
							}
						}
				}
			}

			// Now we need to get all the blocks relative to the origin (interface)
			val blocks = mutableMapOf<OriginRelative, String>()
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
						val location = OriginRelative(relativeX, relativeY, relativeZ)

						// Add the block to the multiblock configuration
						material?.let { blocks[location] = material }
					}
				}
			}
			newMultiblocks.add(
				MultiblockType(
					multiblockName,
					blocks
				)
			)
		}
		klogger.info {
			"Loaded ${newMultiblocks.size} multiblock types:\n   " +
					newMultiblocks.joinToString("\n   ") { it.name + ": " + it.blocks.size }
		}
		klogger.info {
			"Interface Block Types:\n    " +
					interfaceBlockTypes.joinToString("\n   ")
		}
		types = newMultiblocks
	}


	/**
	 * The multiblocks in this chunk
	 * Backed by the Chunk's PDC
	 */
	private var Chunk.savedMultiblocks: Set<MultiblockInstance>
		get() = this.persistentDataContainer.get(
			NamespacedKey(plugin, "multiblocks"),
			MultiblockPDC
		) ?: emptySet()
		set(value) = this.persistentDataContainer.set(
			NamespacedKey(plugin, "multiblocks"),
			MultiblockPDC,
			value
		)
}
