package io.github.hydrazinemc.hydrazine.projectiles

import io.github.hydrazinemc.hydrazine.utils.extensions.id
import org.bukkit.Color
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class TemporaryBlasterListener: Listener {
	@EventHandler
	fun onRightClickWithBlaster(event: PlayerInteractEvent) {
		if (event.action != Action.LEFT_CLICK_AIR && event.action != Action.LEFT_CLICK_BLOCK) return
		if (event.item?.id != "blaster") return
		ParticleProjectile(event.player.eyeLocation, Color.AQUA, 4, 200, 3.0, 5, 5.0, 5f).shootProjectile()
		event.isCancelled = true;
	}
}