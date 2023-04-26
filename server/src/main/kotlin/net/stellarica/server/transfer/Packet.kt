package net.stellarica.server.transfer

import kotlinx.serialization.Serializable
import net.minecraft.core.BlockPos

@Serializable
sealed interface Packet {
	var previousNode: BlockPos?
}