package net.stellarica.server.transfer.node

import kotlinx.serialization.Serializable
import net.stellarica.common.util.OriginRelative

@Serializable
sealed interface Node {
	val pos: OriginRelative
	var connections: MutableSet<OriginRelative>
	var content: Int
	var capacity: Int
	var inputBuffer: Int
	var outputBuffer: Int
}