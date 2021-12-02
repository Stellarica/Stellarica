package io.github.petercrawley.minecraftstarshipplugin.multiblocks

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.plugin
import io.github.petercrawley.minecraftstarshipplugin.customMaterials.MSPMaterial
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class MultiblockDetectionListener: Listener {
	@EventHandler
	fun onMultiblockDetection(event: PlayerInteractEvent) {
		if (event.action != Action.RIGHT_CLICK_BLOCK) return

		val block = MSPMaterial(event.clickedBlock!!.blockData)

		if (block != MSPMaterial("INTERFACE")) return // Not an interface block

		event.isCancelled = true

		plugin.logger.info("Interface Activation")
	}
}