package io.github.hydrazinemc.hydrazine.starships.listeners

import io.github.hydrazinemc.hydrazine.utils.BlockLocation
import io.github.hydrazinemc.hydrazine.utils.Direction
import io.github.hydrazinemc.hydrazine.utils.extensions.isPilotingShip
import io.github.hydrazinemc.hydrazine.utils.extensions.starship
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent

class StarshipControlListener: Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	fun onPlayerMove(event: PlayerInteractEvent){
		if (!event.player.isPilotingShip) return
		val ship = event.player.starship!!
		if (ship.isMoving) return
		ship.queueMovement(BlockLocation(1,0,0,event.player.world))
	}

	@EventHandler
	fun onPlayerDropItem(event: PlayerDropItemEvent) {
		if (!event.player.isPilotingShip) return
		val ship = event.player.starship!!
		if (ship.isMoving) return
		ship.queueRotation(Math.PI/2)
		event.isCancelled = true
	}
}