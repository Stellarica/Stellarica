package net.stellarica.server.transfer.nodes

import net.stellarica.common.utils.OriginRelative

interface Node {
	val pos: OriginRelative
	val connections: MutableSet<OriginRelative>
	var content: Int
	var inputBuffer: Int
	var outputBuffer: Int
}