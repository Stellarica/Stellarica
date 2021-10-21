package io.github.petercrawley.minecraftstarshipplugin.events

import io.github.petercrawley.minecraftstarshipplugin.projectiles.ParticleProjectile
import org.bukkit.Location
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class ParticleProjectileHitBlockEvent(val projectile: ParticleProjectile, val location: Location) : Event() {

	override fun getHandlers(): HandlerList {
		return handlerList
	}

	companion object {
		val handlerList = HandlerList()
	}
}