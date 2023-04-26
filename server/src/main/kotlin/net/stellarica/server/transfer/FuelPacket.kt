package net.stellarica.server.transfer

import net.minecraft.core.BlockPos

class FuelPacket(val content: Int): Packet {
	override var previousNode: BlockPos? = null
}