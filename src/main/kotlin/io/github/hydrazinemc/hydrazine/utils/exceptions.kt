package io.github.hydrazinemc.hydrazine.utils

import io.github.hydrazinemc.hydrazine.crafts.pilotable.Pilotable
import org.bukkit.entity.Player

class AlreadyMovingException(message: String) : Exception(message)
class AlreadyPilotedException(craft: Pilotable, pilot: Player) :
	Exception("${pilot.name} attempted to pilot $craft, but it was already piloted by ${craft.pilot?.name}")