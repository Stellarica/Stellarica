package net.stellarica.server.transfer.pipes

import net.stellarica.common.utils.OriginRelative

abstract class PipeNode(
	val pos: OriginRelative,
	var content: Int,
	var inputBuffer: Int,
	var outputBuffer: Int
) {
	val connections = mutableSetOf<PipeNode>()

	override fun toString(): String {
		return "pos=$pos, content=$content, inputBuffer=$inputBuffer, outputBuffer=$outputBuffer"
	}
}

class InputNode(
	pos: OriginRelative,
	outputBuffer: Int = 0,
	inputBuffer: Int = 0,
	content: Int = 0
) : PipeNode(pos, content, inputBuffer, outputBuffer)

class Node(
	pos: OriginRelative,
	outputBuffer: Int = 0,
	inputBuffer: Int = 0,
	content: Int = 0
) : PipeNode(pos, content, inputBuffer, outputBuffer)

class OutputNode(
	pos: OriginRelative,
	outputBuffer: Int = 0,
	inputBuffer: Int = 0,
	content: Int = 0
) : PipeNode(pos, content, inputBuffer, outputBuffer)