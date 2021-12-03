package io.github.petercrawley.minecraftstarshipplugin.multiblocks

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.plugin
import io.github.petercrawley.minecraftstarshipplugin.customMaterials.MSPMaterial
import io.github.petercrawley.minecraftstarshipplugin.events.MSPConfigReloadEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class MultiblockDetectionListener: Listener {
	companion object {
		var multiblocks = setOf<MultiblockConfiguration>()
			private set
	}

	@EventHandler
	fun onMultiblockDetection(event: PlayerInteractEvent) {
		if (event.action != Action.RIGHT_CLICK_BLOCK) return
		if (event.hand != EquipmentSlot.HAND) return
		if (event.player.isSneaking) return

		val block = MSPMaterial(event.clickedBlock!!)

		if (block != MSPMaterial("INTERFACE")) return // Not an interface block

		event.isCancelled = true

		// Actually detect it
		val potentialMultiblocks = mutableSetOf<MultiblockConfiguration>()

		// Start by iterating over each multiblock
		multiblocks.forEach multiblockLoop@ { multiblockConfiguration ->
			fun check(rotationFunction: (MultiblockOriginRelativeLocation) -> MultiblockOriginRelativeLocation) {
				// Then check each block in the multiblock
				multiblockConfiguration.blocks.forEach {
					// Rotated location
					val rotatedLocation = rotationFunction(it.key)

					// Get the block relative to the interface block
					val relativeBlock = MSPMaterial(event.clickedBlock!!.getRelative(rotatedLocation.x, rotatedLocation.y, rotatedLocation.z))

					// Check if the actual material of the block matches the expected material
					if (relativeBlock != it.value) { // If it does not match then we have the wrong multiblock.
						plugin.logger.info("Found unexpected block! Found $relativeBlock expected ${it.value}")
						return
					}
				}

				potentialMultiblocks.add(multiblockConfiguration)
			}

			check { it }
			check { MultiblockOriginRelativeLocation(-it.z, it.y, it.x) }
			check { MultiblockOriginRelativeLocation(-it.x, it.y, -it.z) }
			check { MultiblockOriginRelativeLocation(it.z, it.y, -it.x) }
		}

		// Tiebreaker
		val multiblock = potentialMultiblocks.sortedBy { it.blocks.size }.lastOrNull()

		if (multiblock == null) {
			plugin.logger.info("No multiblock found")
			return
		}

		plugin.logger.info("Found multiblock! ${multiblock.name}")
	}

	@EventHandler
	fun onMSPConfigReload(event: MSPConfigReloadEvent) {
		val newMultiblocks = mutableSetOf<MultiblockConfiguration>()
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
			val multiblockConfiguration = MultiblockConfiguration(multiblock)

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
						val location = MultiblockOriginRelativeLocation(relativeX, relativeY, relativeZ)

						// Add the block to the multiblock configuration
						multiblockConfiguration.blocks[location] = material!!
					}
				}
			}

			newMultiblocks.add(multiblockConfiguration)
		}

		multiblocks = newMultiblocks
	}
}