import org.bukkit.block.BlockFace

fun rotateBlockFace(face: BlockFace, theta: Double): BlockFace? {
	return when (theta) {
		Math.PI / 2 -> rotateCardinalFaceRight(face)
		Math.PI / -2 -> rotateCardinalFaceLeft(face)
		Math.PI -> rotateCardinalFaceOpposite(face)
		0.0 -> face
		Math.PI * 2 -> face
		else -> null
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