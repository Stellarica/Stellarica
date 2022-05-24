import io.github.hydrazinemc.hydrazine.utils.rotation.RotationAmount
import io.github.hydrazinemc.hydrazine.utils.rotation.rotateAxis
import org.bukkit.block.BlockFace
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.Directional
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
 * Works with [Directional]s, [Orientable]s, and [Rotatable]s
 * and will silently fail if this is not one of those.
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
}
