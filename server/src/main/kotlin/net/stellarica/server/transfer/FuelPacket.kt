package net.stellarica.server.transfer

import kotlinx.serialization.Serializable
import net.minecraft.core.BlockPos
import net.stellarica.server.util.BlockPosSerializer

@Serializable
class FuelPacket(val content: Int): Packet {
	@Serializable(with=BlockPosSerializer::class)
	override var previousNode: BlockPos? = null
}