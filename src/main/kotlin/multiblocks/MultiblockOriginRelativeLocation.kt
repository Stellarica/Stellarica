package io.github.petercrawley.minecraftstarshipplugin.multiblocks

data class MultiblockOriginRelativeLocation (val x: Int, val y: Int, val z: Int) {
	override fun toString(): String {
		return "($x, $y, $z)"
	}
}