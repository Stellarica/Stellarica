package net.stellarica.server.event

import net.stellarica.server.StellaricaServer.Companion.plugin
import org.bukkit.event.EventPriority

typealias BukkitEvent = org.bukkit.event.Event
typealias BukkitListener = org.bukkit.event.Listener
typealias BukkitPriority = EventPriority

private typealias BukkitEventHandler = org.bukkit.event.EventHandler

fun <E: BukkitEvent> listen(l: BukkitListener.(E) -> Unit, priority: BukkitPriority = BukkitPriority.NORMAL) {

	// #######################################
	// You might be looking at this, and thinking "this looks like a complete mess"
	// You, would in fact, be correct.
	// Why is it like this, you might ask?
	// See https://discord.com/channels/1038493335679156425/1038522118176002178/1150957224198410391
	// or if that's gone uh, it was basically because setting the event priority at runtime was pain
	//
	/// these are all errors that I came across trying to do it:
	// "Kotlin reflection not yet supported for synthetic java properties"
	// "NullPointerException"
	// "org.jetbrains.kotlin.backend.common.BackendException: Backend internal error: Exception during IR lowering"
	// "InaccessibleObjectException: Unable to make java.lang.Class$AnnotationData annotationData() accessible"
	// "IllegalPluginAccessException: Unable to find handler list for org.bukkit.event.Event"
	//
	// Great way to waste an hour.
	// Feel free to try to do it.
	// Do recommend.
	// (/s)
	// #######################################
	plugin.server.pluginManager.registerEvents(when(priority) {
		EventPriority.LOWEST -> object : org.bukkit.event.Listener {
			@BukkitEventHandler(priority = EventPriority.LOWEST)
			fun onEvent(event: E) {
				this.l(event)
			}
		}
		EventPriority.LOW -> object : org.bukkit.event.Listener {
			@BukkitEventHandler(priority = EventPriority.LOW)
			fun onEvent(event: E) {
				this.l(event)
			}
		}
		EventPriority.NORMAL -> object : org.bukkit.event.Listener {
			@BukkitEventHandler(priority = EventPriority.NORMAL)
			fun onEvent(event: E) {
				this.l(event)
			}
		}
		EventPriority.HIGH -> object : org.bukkit.event.Listener {
			@BukkitEventHandler(priority = EventPriority.HIGH)
			fun onEvent(event: E) {
				this.l(event)
			}
		}
		EventPriority.HIGHEST-> object : org.bukkit.event.Listener {
			@BukkitEventHandler(priority = EventPriority.HIGHEST)
			fun onEvent(event: E) {
				this.l(event)
			}
		}
		EventPriority.MONITOR -> object : org.bukkit.event.Listener {
			@BukkitEventHandler(priority = EventPriority.MONITOR)
			fun onEvent(event: E) {
				this.l(event)
			}
		}
	}, plugin)
}
