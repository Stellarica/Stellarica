package net.stellarica.server.event

import net.stellarica.server.StellaricaServer.Companion.plugin

typealias BukkitEvent = org.bukkit.event.Event
typealias BukkitListener = org.bukkit.event.Listener
typealias BukkitPriority = org.bukkit.event.EventPriority

inline fun <reified T : BukkitEvent> listen(
	noinline block: BukkitListener.(T) -> Unit,
	priority: BukkitPriority = BukkitPriority.NORMAL,
	ignoreCancelled: Boolean = false
) {
	// a mild case of the cursed
	plugin.server.pluginManager.registerEvent(
		T::class.java,
		object : BukkitListener {},
		priority,
		{ listener, event -> listener.block(event as? T ?: return@registerEvent) },
		plugin,
		ignoreCancelled
	)
}
