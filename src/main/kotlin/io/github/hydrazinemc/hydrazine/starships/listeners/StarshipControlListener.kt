package io.github.hydrazinemc.hydrazine.starships.listeners

import io.github.hydrazinemc.hydrazine.utils.isPilotingShip
import io.github.hydrazinemc.hydrazine.utils.starship
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class StarshipControlListener: Listener {
	@EventHandler
	fun onPlayerClick(event: PlayerInteractEvent){
		if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.LEFT_CLICK_AIR ) return;
		if (!event.player.isPilotingShip) return
		val ship = event.player.starship
	}
}