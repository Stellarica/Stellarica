package io.github.hydrazinemc.hydrazine.server.utils

import io.github.hydrazinemc.hydrazine.server.crafts.pilotables.Pilotable
import org.bukkit.entity.Player

// todo: these are dumb, don't need exceptions for every little thing
/**
 * Thrown when a Craft is already moving
 */
class AlreadyMovingException(message: String) : Exception(message)

/**
 * Thrown when a Pilotable is already piloted
 */
class AlreadyPilotedException(craft: Pilotable, pilot: Player) :
	Exception("${pilot.name} attempted to pilot $craft, but it was already piloted by ${craft.pilot?.name}")
