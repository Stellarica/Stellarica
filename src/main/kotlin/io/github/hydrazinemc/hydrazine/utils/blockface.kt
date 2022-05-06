import io.github.hydrazinemc.hydrazine.utils.RotationAmount
import org.bukkit.block.BlockFace

/**
 * Rotate [face] by [RotationAmount]
 * Only works with cardinal faces (north, east, etc.)
 */
fun rotateBlockFace(face: BlockFace, rotationAmount: RotationAmount): BlockFace {
	return when (rotationAmount) {
		// inverting this is counter-intuitive but things dont seem to rotate correctly otherwise /shrug
		// I guess its counterclockwise looking up from below?
		RotationAmount.CLOCKWISE -> rotateCardinalFaceLeft(face)!!
		RotationAmount.COUNTERCLOCKWISE -> rotateCardinalFaceRight(face)!!
		RotationAmount.REVERSE -> rotateCardinalFaceOpposite(face)!!
		RotationAmount.NONE -> face
	}
}

/**
 * @see rotateBlockFace
 */
fun rotateCardinalFaceLeft(face: BlockFace): BlockFace? {
	return when (face) {
		BlockFace.EAST -> BlockFace.NORTH
		BlockFace.SOUTH -> BlockFace.EAST
		BlockFace.WEST -> BlockFace.SOUTH
		BlockFace.NORTH -> BlockFace.WEST
		else -> null
	}
}

/**
 * @see rotateBlockFace
 */
fun rotateCardinalFaceRight(face: BlockFace): BlockFace? {
	return when (face) {
		BlockFace.WEST -> BlockFace.NORTH
		BlockFace.NORTH -> BlockFace.EAST
		BlockFace.EAST -> BlockFace.SOUTH
		BlockFace.SOUTH -> BlockFace.WEST
		else -> null
	}
}

/**
 * @see rotateBlockFace
 */
fun rotateCardinalFaceOpposite(face: BlockFace): BlockFace? {
	return when (face) {
		BlockFace.WEST -> BlockFace.EAST
		BlockFace.NORTH -> BlockFace.SOUTH
		BlockFace.EAST -> BlockFace.WEST
		BlockFace.SOUTH -> BlockFace.NORTH
		else -> null
	}
}