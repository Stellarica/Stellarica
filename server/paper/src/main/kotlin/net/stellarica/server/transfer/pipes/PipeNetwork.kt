package net.stellarica.server.transfer.pipes

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.Blocks
import net.stellarica.common.utils.OriginRelative
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DefaultUndirectedGraph

class PipeNetwork(
	val origin: BlockPos,
	val world: ServerLevel
) {
	var direction = Direction.NORTH

	val graph = DefaultUndirectedGraph<PipeNode, DefaultEdge>(DefaultEdge::class.java)

	fun detect() {
		val start = System.currentTimeMillis()

		val undirectedNodes = mutableSetOf<Pair<OriginRelative, OriginRelative>>()
		val inputsPositions = mutableSetOf<OriginRelative>()
		println("Starting node detection")
		detectConnectedPairs(OriginRelative(0,0,0), undirectedNodes, mutableSetOf(OriginRelative(0,0,0)), inputsPositions)
		for ((p1, p2) in undirectedNodes) {
			val n1 = PipeNode(p1)
			val n2 = PipeNode(p2)
			graph.addVertex(n1)
			graph.addVertex(n2)
			graph.addEdge(n1, n2)
		}
		println("Elapsed time ${System.currentTimeMillis() - start}ms")
		println("Graph: $graph")
	}

	private fun detectConnectedPairs(
		pos: OriginRelative,
		nodes: MutableSet<Pair<OriginRelative, OriginRelative>>,
		detected: MutableSet<OriginRelative>,
		inputs: MutableSet<OriginRelative>
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
							detected.add(next)
							detectConnectedPairs(next, nodes, detected, inputs)
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
	private fun pos(pos: OriginRelative) = pos.getBlockPos(origin, direction)
}

private operator fun OriginRelative.plus(other: OriginRelative) = OriginRelative(x + other.x, y + other.y, z + other.z)
private operator fun OriginRelative.times(dist: Int) = OriginRelative(x * dist, y * dist, z * dist)

