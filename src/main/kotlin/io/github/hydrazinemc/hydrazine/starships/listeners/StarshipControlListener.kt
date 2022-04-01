package io.github.hydrazinemc.hydrazine.starships.listeners

import io.github.hydrazinemc.hydrazine.utils.BlockLocation
import io.github.hydrazinemc.hydrazine.utils.isPilotingShip
import io.github.hydrazinemc.hydrazine.utils.starship
import org.bukkit.FluidCollisionMode
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import kotlin.math.roundToInt

class StarshipControlListener: Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	fun onPlayerMove(event: PlayerInteractEvent){
		if (!event.player.isPilotingShip) return
		val ship = event.player.starship!!
		if (ship.isMoving) return
		ship.queueMovement(BlockLocation(1,0,0,event.player.world))
	}
}