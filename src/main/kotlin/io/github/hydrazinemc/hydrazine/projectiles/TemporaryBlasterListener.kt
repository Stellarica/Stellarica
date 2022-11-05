package io.github.hydrazinemc.hydrazine.projectiles

import com.destroystokyo.paper.ParticleBuilder
import io.github.hydrazinemc.hydrazine.customitems.power
import io.github.hydrazinemc.hydrazine.utils.Tasks
import io.github.hydrazinemc.hydrazine.utils.extensions.id
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.util.Vector
import java.util.UUID
import kotlin.math.roundToInt

class TemporaryBlasterListener : Listener {
	private val drills = mutableSetOf<UUID>()

	/*
	This is all temporary code, designed for early prototyping and testing
	Anything goes
	 */

	init {
		Tasks.syncRepeat(1, 1) {
			Bukkit.getServer().onlinePlayers.forEach { player ->
				if (!drills.contains(player.uniqueId)) return@forEach
				if(player.inventory.itemInMainHand.id != "laser_drill") return@forEach

				// note that this doesn't actually check if there's enough
				player.inventory.itemInMainHand.power = player.inventory.itemInMainHand.power?.minus(1)

				player.world.rayTraceBlocks(player.eyeLocation, player.eyeLocation.direction, 20.0)?.let {

					it.hitBlock?.breakNaturally()
					player.world.playSound(player.location, Sound.ENTITY_BEE_DEATH, 0.7f, 0.5f)

					// spawn particle beam
					val range = it.hitPosition.distance(player.eyeLocation.toVector()).roundToInt()
					val loc = player.eyeLocation.clone()
					val direction: Vector = player.eyeLocation.direction
					for (i in 0..range * 4) {
						loc.add(direction.clone().multiply(0.25))
						loc.world.spawnParticle(Particle.SOUL_FIRE_FLAME, loc, 1, 0.0, 0.0, 0.0, 0.0, null, true)
					}
				}
			}
		}
	}

	@EventHandler
	fun onRightClickWithBlaster(event: PlayerInteractEvent) {
		if (event.action != Action.LEFT_CLICK_AIR && event.action != Action.LEFT_CLICK_BLOCK) return
		if (event.item?.id != "blaster") return
		ParticleProjectile(event.player.eyeLocation, Color.AQUA, 4, 200, 3.0, 5, 5.0, 5f).shootProjectile()
		event.isCancelled = true
	}

	@EventHandler
	fun onLeftClickWithLaserDrill(event: PlayerInteractEvent) {
		if (event.action != Action.LEFT_CLICK_AIR && event.action != Action.LEFT_CLICK_BLOCK) return
		if (event.item?.id != "laser_drill") return
		if (drills.remove(event.player.uniqueId) == false) drills.add(event.player.uniqueId)

		event.isCancelled = true
	}
}