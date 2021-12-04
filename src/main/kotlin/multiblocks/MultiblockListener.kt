package io.github.petercrawley.minecraftstarshipplugin.multiblocks

import com.destroystokyo.paper.event.server.ServerTickStartEvent
import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.plugin
import io.github.petercrawley.minecraftstarshipplugin.customMaterials.MSPMaterial
import io.github.petercrawley.minecraftstarshipplugin.events.MSPConfigReloadEvent
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.TextColor.color
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class MultiblockListener: Listener {
	companion object {
		var multiblocks = setOf<MultiblockLayout>()
			private set
	}

	private fun validate(rotation: Byte, layout: MultiblockLayout, origin: Block) : Boolean {
		fun rotationFunction(it: MultiblockOriginRelative) = when (rotation) {
			1.toByte() -> { it }
			2.toByte() -> { MultiblockOriginRelative(-it.z, it.y, it.x) }
			3.toByte() -> { MultiblockOriginRelative(-it.x, it.y, -it.z) }
			4.toByte() -> { MultiblockOriginRelative(it.z, it.y, -it.x) }
			else -> throw IllegalArgumentException("Invalid rotation: $rotation")
		}

		layout.blocks.forEach {
			val rotatedLocation = rotationFunction(it.key)

			val relativeBlock = MSPMaterial(origin.getRelative(rotatedLocation.x, rotatedLocation.y, rotatedLocation.z))

			if (relativeBlock != it.value) return false // A block we were expecting is missing, so break the function.
		}

		return true // Valid, save it and keep looking.
	}

	@EventHandler
	fun onMultiblockDetection(event: PlayerInteractEvent) {
		if (event.action != Action.RIGHT_CLICK_BLOCK || event.hand != EquipmentSlot.HAND || event.player.isSneaking) return

		val clickedBlock = event.clickedBlock!!
		val clickedBlockMaterial = MSPMaterial(clickedBlock)

		if (clickedBlockMaterial != MSPMaterial("INTERFACE")) return // Not an interface block

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
			event.player.sendMessage(text("Multiblock is invalid.").color(color(0xcc0000)))
			return
		}

		event.player.sendMessage(text("Found Multiblock: ${multiblock.key.name}").color(color(0x008800)))

		val multiblockNamespacedKey = NamespacedKey(plugin, "multiblocks")

		// Get the multiblock list, or create it if it doesn't exist
		val multiblockArray = clickedBlock.chunk.persistentDataContainer.get(multiblockNamespacedKey, MultiblockPDC()) ?: mutableSetOf()

		// Add the multiblock to the list
		multiblockArray.add(Multiblock(multiblock.key.name, clickedBlock.x, clickedBlock.y, clickedBlock.z, multiblock.value))

		// Save it
		clickedBlock.chunk.persistentDataContainer.set(multiblockNamespacedKey, MultiblockPDC(), multiblockArray)
	}

	@EventHandler
	fun onChunkTick(event: ServerTickStartEvent) {
		Bukkit.getWorlds().forEach { world ->
			world.loadedChunks.forEach chunk@ { chunk ->
				// Get the multiblock list of a chunk
				val multiblockArray = chunk.persistentDataContainer.get(NamespacedKey(plugin, "multiblocks"), MultiblockPDC()) ?: return@chunk

				// Iterate over each multiblock
				multiblockArray.forEach multiblock@ { multiblock ->
					// Get the layout
					val multiblockLayout = multiblocks.find { it.name == multiblock.name }

					// If the layout does not exist, undetect the multiblock
					if (multiblockLayout == null) {
						plugin.logger.warning("Chunk ${chunk.x}, ${chunk.z} contains a non-existent multiblock: ${multiblock.name}, it has been undetected.")

						multiblockArray.remove(multiblock)
						chunk.persistentDataContainer.set(NamespacedKey(plugin, "multiblocks"), MultiblockPDC(), multiblockArray)

						return@multiblock
					}

					// Validate the layout
					if (!validate(multiblock.r, multiblockLayout, world.getBlockAt(multiblock.x, multiblock.y, multiblock.z))) {
						plugin.logger.warning("Chunk ${chunk.x}, ${chunk.z} contains an invalid multiblock: ${multiblock.name}, it has been undetected.")

						multiblockArray.remove(multiblock)
						chunk.persistentDataContainer.set(NamespacedKey(plugin, "multiblocks"), MultiblockPDC(), multiblockArray)

						return@multiblock
					}
				}
			}
		}
	}

	@EventHandler
	fun onMSPConfigReload(event: MSPConfigReloadEvent) {
		val newMultiblocks = mutableSetOf<MultiblockLayout>()
		plugin.config.getConfigurationSection("multiblocks")?.getKeys(false)?.forEach multiblockLoop@{ multiblock ->
			// The first thing that needs to be done is we need to get all the keys for the multiblock
			// This way we know what blocks are in the multiblock
			val keys = mutableMapOf<String, MSPMaterial>()
			var interfaceKey: Char? = null

			plugin.config.getConfigurationSection("multiblocks.$multiblock.key")!!.getKeys(false).forEach {
				val materialString = plugin.config.getString("multiblocks.$multiblock.key.$it")!!

				val material = MSPMaterial(materialString)

				if (keys.containsValue(material)) {
					plugin.logger.severe("Multiblock $multiblock contains duplicate material $materialString")
					return@multiblockLoop
				}

				// TODO: Interface should be determined by a config file.
				if (materialString == "INTERFACE") interfaceKey = it[0]

				keys[it] = material
			}

			if (interfaceKey == null) {
				plugin.logger.severe("Multiblock $multiblock does not have an interface block")
				return@multiblockLoop
			}

			// Now we need to find the interface as all blocks in a multtiblock are stored relative to this point.
			val layers = plugin.config.getConfigurationSection("multiblocks.$multiblock.layers")!!.getKeys(false)

			var interfaceY: Int? = null
			var interfaceZ: Int? = null
			var interfaceX: Int? = null

			run layerLoop@ {
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

			// Create a MultiblockConfiguration
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
						val material = keys[xChar.toString()]

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