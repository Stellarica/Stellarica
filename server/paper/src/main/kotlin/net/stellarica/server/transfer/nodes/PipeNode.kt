package net.stellarica.server.transfer.nodes

import kotlinx.serialization.Serializable
import net.stellarica.common.utils.OriginRelative

@Serializable
abstract class PipeNode: Node {
	override var outputBuffer: Int = 0
	override var inputBuffer: Int = 0
	override var content: Int = 0
	override val connections: MutableSet<OriginRelative> = mutableSetOf()
}

@Serializable
class PipeInputNode(
	override var pos: OriginRelative,
) : PipeNode()

@Serializable
class NormalPipeNode(
	override var pos: OriginRelative,
) : PipeNode()

@Serializable
class PipeOutputNode(
	override var pos: OriginRelative,
) : PipeNode()