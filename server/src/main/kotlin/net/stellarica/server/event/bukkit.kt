package net.stellarica.server.event

import net.stellarica.server.StellaricaServer.Companion.plugin

typealias BukkitEvent = org.bukkit.event.Event
typealias BukkitListener = org.bukkit.event.Listener

fun <E: BukkitEvent> listen(l: BukkitListener.(E) -> Unit) {
	plugin.server.pluginManager.registerEvents(object : org.bukkit.event.Listener {
		@org.bukkit.event.EventHandler
		fun onEvent(event: E) {
			this.l(event)
		}
	}, plugin)
}
