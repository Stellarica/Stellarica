package io.github.petercrawley.minecraftstarshipplugin.customMaterials

fun bitOfByte(byte: Byte, bit: Int): Boolean {
	return ((byte.toInt() shr bit) and 1) == 1
}