package io.github.petercrawley.minecraftstarshipplugin

fun Byte.bit(bit: Int): Boolean = ((this.toInt() shr bit) and 1) == 1