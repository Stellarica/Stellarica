package io.github.hydrazinemc.hydrazine.server.utils.rotation

import io.github.hydrazinemc.hydrazine.common.utils.rotation.RotationAmount
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.Directional
import org.bukkit.block.data.MultipleFacing
import org.bukkit.block.data.Orientable
import org.bukkit.block.data.Rotatable

/**
 * Rotate [face] by [RotationAmount]
 * Only works with cardinal faces (north, east, etc.)
 */
fun rotateBlockFace(face: BlockFace, rotationAmount: RotationAmount): BlockFace {
	return when (rotationAmount) {
		// inverting this is counter-intuitive but things dont seem to rotate correctly otherwise /shrug
		// I guess its counterclockwise looking up from below?
		RotationAmount.CLOCKWISE -> rotateCardinalFaceLeft(face)
		RotationAmount.COUNTERCLOCKWISE -> rotateCardinalFaceRight(face)
		RotationAmount.REVERSE -> rotateCardinalFaceOpposite(face)
		RotationAmount.NONE -> face
	}
}

/**
 * @see rotateBlockFace
 */
fun rotateCardinalFaceLeft(face: BlockFace): BlockFace {
	return when (face) {
		BlockFace.EAST -> BlockFace.NORTH
		BlockFace.SOUTH -> BlockFace.EAST
		BlockFace.WEST -> BlockFace.SOUTH
		BlockFace.NORTH -> BlockFace.WEST
		else -> face
	}
}

/**
 * @see rotateBlockFace
 */
fun rotateCardinalFaceRight(face: BlockFace): BlockFace {
	return when (face) {
		BlockFace.WEST -> BlockFace.NORTH
		BlockFace.NORTH -> BlockFace.EAST
		BlockFace.EAST -> BlockFace.SOUTH
		BlockFace.SOUTH -> BlockFace.WEST
		else -> face
	}
}

/**
 * @see rotateBlockFace
 */
fun rotateCardinalFaceOpposite(face: BlockFace): BlockFace {
	return when (face) {
		BlockFace.WEST -> BlockFace.EAST
		BlockFace.NORTH -> BlockFace.SOUTH
		BlockFace.EAST -> BlockFace.WEST
		BlockFace.SOUTH -> BlockFace.NORTH
		else -> face
	}
}

/**
 * Rotate this data by [amount]
 *
 * Works with [Directional]s, [Orientable]s, [MultipleFacing]s, and [Rotatable]s
 * and will silently fail if this is not one of those.
 *
 * Ignores mushroom stems as they are used for custom blocks.
 */
fun BlockData.rotate(amount: RotationAmount) {
	if (amount == RotationAmount.NONE) return
	// Handle rotation of Directionals
	if (this is Directional) {
		this.facing = rotateBlockFace(this.facing, amount)
	}
	// Rotation of Orientables
	if (this is Orientable && amount != RotationAmount.REVERSE) {
		this.axis = rotateAxis(this.axis)
	}
	// Rotation of Rotatables
	if (this is Rotatable) {
		this.rotation = rotateBlockFace(this.rotation, amount)
	}
	// Rotation of MultipleFacings (Iron Bars, etc.)
	if (this is MultipleFacing) {
		if (this.material == Material.MUSHROOM_STEM) return // custom blocks
		val newFaces = mutableMapOf<BlockFace, Boolean>()
		this.faces.forEach { face ->
			newFaces[rotateBlockFace(face, amount)] = this.hasFace(face) // get the rotated faces
		} // this could be code golfed
		this.allowedFaces.forEach { this.setFace(it, false) } // set all faces false
		newFaces.forEach { this.setFace(it.key, it.value) } // apply rotated faces
	}
}
