package io.github.petercrawley.minecraftstarshipplugin.multiblocks

data class Multiblock(val name: String, val x: Int, val y: Int, val z: Int, val r: Byte) {
	override fun toString(): String {
		return "Multiblock(name='$name', x=$x, y=$y, z=$z, r=$r)"
	}
}