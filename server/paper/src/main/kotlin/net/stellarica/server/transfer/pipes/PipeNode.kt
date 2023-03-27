package net.stellarica.server.transfer.pipes

import net.stellarica.common.utils.OriginRelative

interface PipeNode {
	val pos: OriginRelative
	var content: Int
	var inputBuffer: Int
	var outputBuffer: Int
}

data class InputNode(
	override val pos: OriginRelative,
	override var outputBuffer: Int = 0,
	override var inputBuffer: Int = 0,
	override var content: Int = 0
) : PipeNode

data class Node(
	override val pos: OriginRelative,
	override var outputBuffer: Int = 0,
	override var inputBuffer: Int = 0,
	override var content: Int = 0
) : PipeNode

data class OutputNode(
	override val pos: OriginRelative,
	override var outputBuffer: Int = 0,
	override var inputBuffer: Int = 0,
	override var content: Int = 0
) : PipeNode