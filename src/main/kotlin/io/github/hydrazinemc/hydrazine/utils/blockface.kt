import io.github.hydrazinemc.hydrazine.utils.RotationAmount
import org.bukkit.block.BlockFace

fun rotateBlockFace(face: BlockFace, rotationAmount: RotationAmount): BlockFace {
	return when (rotationAmount) {
		RotationAmount.CLOCKWISE -> rotateCardinalFaceRight(face)!!
		RotationAmount.COUNTERCLOCKWISE -> rotateCardinalFaceLeft(face)!!
		RotationAmount.REVERSE -> rotateCardinalFaceOpposite(face)!!
		RotationAmount.NONE -> face
	}
}


fun rotateCardinalFaceLeft(face: BlockFace): BlockFace? {
	return when (face) {
		BlockFace.EAST -> BlockFace.NORTH
		BlockFace.SOUTH -> BlockFace.EAST
		BlockFace.WEST -> BlockFace.SOUTH
		BlockFace.NORTH -> BlockFace.WEST
		else -> null
	}
}

fun rotateCardinalFaceRight(face: BlockFace): BlockFace? {
	return when (face) {
		BlockFace.WEST -> BlockFace.NORTH
		BlockFace.NORTH -> BlockFace.EAST
		BlockFace.EAST -> BlockFace.SOUTH
		BlockFace.SOUTH -> BlockFace.WEST
		else -> null
	}
}

fun rotateCardinalFaceOpposite(face: BlockFace): BlockFace? {
	return when (face) {
		BlockFace.WEST -> BlockFace.EAST
		BlockFace.NORTH -> BlockFace.SOUTH
		BlockFace.EAST -> BlockFace.WEST
		BlockFace.SOUTH -> BlockFace.NORTH
		else -> null
	}
}