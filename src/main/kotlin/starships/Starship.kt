package io.github.petercrawley.minecraftstarshipplugin.starships

import io.github.petercrawley.minecraftstarshipplugin.customblocks.MSPMaterial
import io.github.petercrawley.minecraftstarshipplugin.misc.MSPBlockLocation
import org.bukkit.block.Block
import org.bukkit.entity.Player

data class Starship(var owner: Player, var pilot: Player?) {
	var detectedBlocks = mutableSetOf<MSPBlockLocation>() // Blocks that we know are part of the ship.
	var allowedBlocks  = mutableSetOf<MSPMaterial>()      // Blocks that have been specifically allowed.

	constructor(origin: Block, user: Player) : this(user, user) {
		detectedBlocks = mutableSetOf(MSPBlockLocation(origin))
	}
}