package net.stellarica.server.transfer.pipes

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.Blocks
import net.stellarica.common.utils.OriginRelative


class PipeNetwork(
	val origin: BlockPos,
	val world: ServerLevel
) {
	var direction = Direction.NORTH

	var nodes = mutableMapOf<OriginRelative, PipeNode>()

	fun detect() {
		val start = System.currentTimeMillis()

		val undirectedNodes = mutableSetOf<Pair<OriginRelative, OriginRelative>>()
		val inputs = mutableSetOf<OriginRelative>()
		val outputs = mutableSetOf<OriginRelative>()

		println("Starting node detection")
		detectConnectedPairs(OriginRelative(0,0,0), undirectedNodes, mutableSetOf(OriginRelative(0,0,0)), inputs, outputs)

		fun createNode(pos: OriginRelative): PipeNode {
			return when (pos) {
				in inputs -> InputNode(pos).also { it.content = 400; println("hi input") }
				in outputs -> OutputNode(pos)
				else -> Node(pos)
			}
		}

		for ((p1, p2) in undirectedNodes) {
			val n1 = nodes.getOrPut(p1) { createNode(p1) }
			val n2 =  nodes.getOrPut(p2) { createNode(p2) }
			n2.connections.add(n1)
			n1.connections.add(n2)
		}
		println("Elapsed time ${System.currentTimeMillis() - start}ms")
		//println("Graph: $nodes")
	}


	fun tick() {
		for (node in nodes.values) {

			// get demand from connected nodes
			var demand = 0
			for (other in node.connections) {
				if (other.content < node.content) {
					demand += node.content - other.content
				}
			}
			// if there's no demand, don't do anything
			if (demand <= 0) continue

			// if we can't supply all demand, supply a fraction of it
			// if we have enough fuel to satisfy all demand, this will be 1
			var num = 1f
			while (demand / num > node.content) {
				num++
			}

			for (other in node.connections) {
				if (other.content < node.content) {
					val transfer = ((node.content - other.content) / num).toInt()
					other.inputBuffer += transfer
					node.outputBuffer += transfer
				}
			}
		}

		// apply changes at once
		for (node in nodes.values) {
			node.content += node.inputBuffer
			node.content -= node.outputBuffer
			node.inputBuffer = 0
			node.outputBuffer = 0
		}
	}

	private fun detectConnectedPairs(
		pos: OriginRelative,
		nodes: MutableSet<Pair<OriginRelative, OriginRelative>>,
		detected: MutableSet<OriginRelative>,
		inputs: MutableSet<OriginRelative>,
		outputs: MutableSet<OriginRelative>
	) {
		for (rel in listOf(
			OriginRelative(0,0,1),
			OriginRelative(0,0,-1),
			OriginRelative(0,1,0),
			OriginRelative(0,-1,0),
			OriginRelative(1,0,0),
			OriginRelative(-1,0,0)
		)) {
			if (isRod(pos + rel)) {
				for (dist in 1..50) {
					val next = pos + rel * dist
					if (isRod(next)) continue
					if (isCopper(next)) {
						if (next !in detected) {
							if (isInput(next)) inputs.add(next)
							if (isOutput(next)) outputs.add(next)
							detected.add(next)
							detectConnectedPairs(next, nodes, detected, inputs, outputs)
						}
						if (!nodes.contains(pos to next) && !nodes.contains(next to pos)) nodes.add(pos to next)
					}
					break
				}
			}
		}
	}

	private fun isCopper(pos: OriginRelative) : Boolean = world.getBlockState(pos(pos)).block == Blocks.WAXED_COPPER_BLOCK || isInput(pos)
	private fun isRod(pos: OriginRelative) : Boolean = world.getBlockState(pos(pos)).block == Blocks.LIGHTNING_ROD
	private fun isInput(pos: OriginRelative) : Boolean = world.getBlockState(pos(pos)).block == Blocks.WAXED_CUT_COPPER
	private fun isOutput(pos: OriginRelative) : Boolean = world.getBlockState(pos(pos)).block == Blocks.WAXED_CUT_COPPER_SLAB
	private fun pos(pos: OriginRelative) = pos.getBlockPos(origin, direction)
}

private operator fun OriginRelative.plus(other: OriginRelative) = OriginRelative(x + other.x, y + other.y, z + other.z)
private operator fun OriginRelative.times(dist: Int) = OriginRelative(x * dist, y * dist, z * dist)

