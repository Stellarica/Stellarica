package net.stellarica.server.craft

import net.stellarica.server.event.CancellableEvent
import net.stellarica.server.event.Event
import org.bukkit.entity.Player

object CraftPilotEvent : CancellableEvent<Pair<Pilotable, Player>>()
object CraftUnpilotEvent : Event<Pilotable>()