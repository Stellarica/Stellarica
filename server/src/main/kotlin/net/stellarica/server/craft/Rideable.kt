package net.stellarica.server.craft

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.audience.ForwardingAudience
import org.bukkit.entity.LivingEntity

interface Rideable : ForwardingAudience {
	val passengers: Set<LivingEntity>
	fun addPassenger(passenger: LivingEntity)

	fun removePassenger(passenger: LivingEntity)

	override fun audiences(): MutableIterable<Audience> {
		return passengers.toMutableList()
	}
}