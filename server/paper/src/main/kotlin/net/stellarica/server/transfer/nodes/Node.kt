package net.stellarica.server.transfer.nodes

import kotlinx.serialization.Serializable
import net.stellarica.common.utils.OriginRelative

@Serializable
sealed interface Node {
	val pos: OriginRelative
	var connections: MutableSet<OriginRelative>
	var content: Int
	var inputBuffer: Int
	var outputBuffer: Int
}