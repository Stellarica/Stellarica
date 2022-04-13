package io.github.hydrazinemc.hydrazine.utils

import io.github.hydrazinemc.hydrazine.starships.Starship
import org.bukkit.entity.Player

class AlreadyMovingException(message: String) : Exception(message)
class AlreadyPilotedException(starship: Starship, pilot: Player) :
	Exception("${pilot.name} attempted to pilot $starship, but it was already piloted by ${starship.pilot?.name}")