package net.stellarica.server.transfer.nodes

import net.stellarica.common.utils.OriginRelative

abstract class PipeNode: Node {
	override var outputBuffer: Int = 0
	override var inputBuffer: Int = 0
	override var content: Int = 0
	override val connections: MutableSet<OriginRelative> = mutableSetOf()
}

class PipeInputNode(
	override var pos: OriginRelative,
) : PipeNode()

class NormalPipeNode(
	override var pos: OriginRelative,
) : PipeNode()

class PipeOutputNode(
	override var pos: OriginRelative,
) : PipeNode()