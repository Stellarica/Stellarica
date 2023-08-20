package net.stellarica.server.craft

import org.bukkit.entity.Player

interface Pilotable : Rideable {
	val pilot: Player?

	val isPiloted: Boolean
		get() = pilot != null

	fun pilot(pilot: Player)

	fun unpilot()

	// todo: I don't like this being part of the interface
	fun movePassengers(craft: Craft, data: CraftTransformation)
}