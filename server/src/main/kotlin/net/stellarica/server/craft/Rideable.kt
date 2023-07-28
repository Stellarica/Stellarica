package net.stellarica.server.craft

import net.kyori.adventure.audience.ForwardingAudience
import org.bukkit.entity.LivingEntity

interface Rideable: ForwardingAudience, Craft {
	val passengers: List<LivingEntity>
	fun addPassenger(passenger: LivingEntity)

	fun removePassenger(passenger: LivingEntity)
}