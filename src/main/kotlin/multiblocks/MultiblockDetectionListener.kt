package io.github.petercrawley.minecraftstarshipplugin.multiblocks

import io.github.petercrawley.minecraftstarshipplugin.customMaterials.MSPMaterial
import org.bukkit.block.Sign
import org.bukkit.block.data.type.WallSign
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class MultiblockDetectionListener: Listener {
	fun onMultiblockDetection(event: PlayerInteractEvent) {
		if (event.clickedBlock == null) return // Interacted with air, we don't care

		val block = MSPMaterial(event.clickedBlock!!.blockData)

		if (block != MSPMaterial("INTERFACE")) return // Not an interface block
	}
}