package io.github.hydrazinemc.hydrazine.crafts

import io.github.hydrazinemc.hydrazine.utils.Vector3
import io.github.hydrazinemc.hydrazine.utils.rotation.RotationAmount

/**
 * Container for extra craft move operation data
 */
data class CraftMoveData(
	/**
	 * The craft in question.
	 */
	val craft: Craft,
	/**
	 * Operation applied to all blocks in the craft.
	 */
	val modifier: (Vector3) -> Vector3,
	/**
	 * The amount to rotate all directional blocks and passengers.
	 */
	val rotation: RotationAmount
)
