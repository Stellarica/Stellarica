package io.github.hydrazinemc.hydrazine.crafts

import io.github.hydrazinemc.hydrazine.utils.RotationAmount
import io.github.hydrazinemc.hydrazine.utils.Vector3

data class CraftMoveData(val ship: Craft, val modifier: (Vector3) -> Vector3, val rotation: RotationAmount)
