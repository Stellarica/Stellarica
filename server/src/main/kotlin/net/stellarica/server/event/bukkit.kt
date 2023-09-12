package net.stellarica.server.event

import net.stellarica.server.StellaricaServer.Companion.plugin
import org.bukkit.event.EventPriority

typealias BukkitEvent = org.bukkit.event.Event
typealias BukkitListener = org.bukkit.event.Listener
typealias BukkitPriority = EventPriority

inline fun <reified T : BukkitEvent> listen(
	noinline block: BukkitListener.(T) -> Unit,
	priority: EventPriority = EventPriority.NORMAL,
	ignoreCancelled: Boolean = false
) {
	plugin.server.pluginManager.registerEvent(
		T::class.java,
		object : BukkitListener {},
		priority,
		{ listener, event -> listener.block(event as? T ?: return@registerEvent) },
		plugin,
		ignoreCancelled
	)
}