package io.github.petercrawley.minecraftstarshipplugin.ships

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player

/*
	I am aware that this async task is not properly being shutdown when the plugin is reloaded.
	I will fix it later.

	TODO: Fix.
 */

class Starship(private val origin: MSPLocation, private val owner: Player) {
	private val detectedBlocks: MutableSet<MSPLocation> = mutableSetOf()

	fun detect() {
		Bukkit.getScheduler().runTaskAsynchronously(MinecraftStarshipPlugin.getPlugin(), Runnable {
			owner.sendMessage("Detecting Starship.")

			val checkedBlocks: MutableSet<MSPLocation> = mutableSetOf()
			val blocksToCheck: MutableSet<MSPLocation> = mutableSetOf()

			blocksToCheck.add(origin)

			while (blocksToCheck.isNotEmpty()) {
				if (detectedBlocks.size == 500000) {
					owner.sendMessage("Reached arbitrary detection limit. (500,000)")
					break
				}

				val currentBlock = blocksToCheck.first()
				blocksToCheck.remove(currentBlock)

				if (checkedBlocks.contains(currentBlock)) continue

				checkedBlocks.add(currentBlock)

				val type = currentBlock.bukkit().block.type

				if (type == Material.AIR) continue

				if (MinecraftStarshipPlugin.getPlugin().nonDetectableBlocks.contains(type)) continue

				detectedBlocks.add(currentBlock)

				blocksToCheck.add(currentBlock.add( 1, 0, 0))
				blocksToCheck.add(currentBlock.add(-1, 0, 0))
				blocksToCheck.add(currentBlock.add( 0, 1, 0))
				blocksToCheck.add(currentBlock.add( 0,-1, 0))
				blocksToCheck.add(currentBlock.add( 0, 0, 1))
				blocksToCheck.add(currentBlock.add( 0, 0,-1))
			}

			owner.sendMessage("Detected " + detectedBlocks.size + " blocks.")
		})
	}

	private fun move(x: Int, y: Int, z: Int) {
		Bukkit.getScheduler().runTaskAsynchronously(MinecraftStarshipPlugin.getPlugin(), Runnable {
			var last = 0

			var canMove = true

			for (block in detectedBlocks) {
				if (!detectedBlocks.contains(block.add(x, y, z))) {
					if (!block.add(x, y, z).bukkit().block.type.isAir) {
						canMove = false
						break
					}
				}
			}

			if (!canMove) {
				owner.sendMessage("Obstructed.")
				return@Runnable
			}

			for (block in detectedBlocks) {
				Bukkit.getScheduler().runTask(MinecraftStarshipPlugin.getPlugin(), Runnable {
					val material = block.bukkit().block.type
					block.bukkit().block.type = Material.AIR
					block.x += x
					block.y += y
					block.z += z
					block.bukkit().block.type = material
				})
			}

		})
	}
}