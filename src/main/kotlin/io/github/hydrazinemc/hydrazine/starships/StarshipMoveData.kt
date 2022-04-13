package io.github.hydrazinemc.hydrazine.starships

import io.github.hydrazinemc.hydrazine.utils.RotationAmount
import io.github.hydrazinemc.hydrazine.utils.Vector3

data class StarshipMoveData(val ship: Starship, val modifier: (Vector3) -> Vector3, val rotation: RotationAmount)
