package net.stellarica.server.craft

import org.bukkit.entity.Player

interface Pilotable: Craft {
	val pilot: Player?

	val isPiloted: Boolean
		get() = pilot != null

	fun pilot(pilot: Player)

	fun unpilot()
}