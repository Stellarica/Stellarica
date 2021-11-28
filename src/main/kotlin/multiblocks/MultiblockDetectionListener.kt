package io.github.petercrawley.minecraftstarshipplugin.multiblocks

import org.bukkit.block.Sign
import org.bukkit.block.data.type.WallSign
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class MultiblockDetectionListener: Listener {
	fun onMultiblockDetection(event: PlayerInteractEvent) {
		if (event.clickedBlock == null) return // Interacted with air, we don't care

		val block = event.clickedBlock!!.blockData as Sign

		if (block.line(0).toString() != "[multiblock]") return // Not a multiblock sign, we don't care

		if (event.clickedBlock !is WallSign) { // Not a wall sign, we don't care
			event.player.sendMessage("Only wall signs can be multiblock signs")
			return
		}



	}

}