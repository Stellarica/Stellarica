package io.github.hydrazinemc.hydrazine.common

import net.minecraft.core.BlockPos

const val networkVersion = 1.toByte()

class CommonTest {
	fun doStuff() {
		println("Hello from common!")
		val pos = BlockPos(0, 0, 0)
		println("Did some nms stuff: $pos")
	}
}