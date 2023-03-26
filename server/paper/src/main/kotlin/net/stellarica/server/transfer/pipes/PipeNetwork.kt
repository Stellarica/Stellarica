package net.stellarica.server.transfer.pipes

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.Blocks
import net.stellarica.common.utils.OriginRelative
import net.stellarica.server.transfer.Node
import net.stellarica.server.transfer.NodeNetwork
import net.stellarica.server.transfer.pipes.node.PipeJunction
import net.stellarica.server.transfer.pipes.node.PipeNode
import java.nio.channels.Pipe

class PipeNetwork(
	val origin: BlockPos,
	val world: ServerLevel
) : NodeNetwork<Fuel>() {
	var direction = Direction.NORTH

	override fun detect() {
		inputs.clear()
		val start = System.currentTimeMillis()

		val undirectedNodes = mutableSetOf<Set<OriginRelative>>()
		val inputsPositions = mutableSetOf<OriginRelative>()
		println("Starting node detection")
		detectDown(OriginRelative(0,0,0), undirectedNodes, mutableSetOf(OriginRelative(0,0,0)), inputsPositions)
		println("Found undirected nodes:\n${
			undirectedNodes.joinToString("\n") {
				"${it.first()} <-> ${it.last()}".replace(
					"OriginRelative",
					""
				)
			}
		}")
		println("Sorting nodes")
		println("Input Positons: $inputsPositions")
		inputsPositions.forEach { inputs.add(constructNodes(it, undirectedNodes, inputsPositions.toMutableSet())) }
		println("Input Nodes: $inputs")

		fun printOut(node: PipeNode, depth: Int) {
			println("${"\t".repeat(depth)}${node.pos}")
			node.outgoingConnections.forEach {
				printOut(it as PipeNode, depth + 1)
			}
		}
		inputs.forEach {
			printOut(it as PipeNode, 0)
		}
		println("Elapsed time ${System.currentTimeMillis() - start}ms")
	}

	fun constructNodes(nodePos: OriginRelative, positions: MutableSet<Set<OriginRelative>>, constructedNodes: MutableSet<OriginRelative>) : Node<Fuel> {
		val node = PipeJunction(nodePos)
		constructedNodes.add(nodePos)
		positions.filter { it.contains(nodePos) }.forEach { connection ->
			val other = connection.toMutableSet().also {it.remove(nodePos)}.first()
			if (other !in constructedNodes) {
				node.outgoingConnections.add(constructNodes(other, positions, constructedNodes))
				constructedNodes.add(other)
			}
		}
		return node
	}

	fun detectDown(
		pos: OriginRelative,
		nodes: MutableSet<Set<OriginRelative>>,
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
				for (dist in 1..20) {
					val next = pos + rel * dist
					if (!isRod(next)) {
						if (isCopper(next)) {
							if (next !in detected) {
								if (isInput(next)) inputs.add(next)
								detected.add(next)
								detectDown(next, nodes, detected, inputs)
							}
							nodes.add(setOf(pos, next))
						}
						break
					}
				}
			}
		}
	}

	fun isCopper(pos: OriginRelative) : Boolean = world.getBlockState(pos(pos)).block == Blocks.WAXED_COPPER_BLOCK || isInput(pos)
	fun isRod(pos: OriginRelative) : Boolean = world.getBlockState(pos(pos)).block == Blocks.LIGHTNING_ROD
	fun isInput(pos: OriginRelative) : Boolean = world.getBlockState(pos(pos)).block == Blocks.WAXED_CUT_COPPER
	private fun pos(pos: OriginRelative) = pos.getBlockPos(origin, direction)
}

operator fun OriginRelative.plus(other: OriginRelative) = OriginRelative(x + other.x, y + other.y, z + other.z)
operator fun OriginRelative.times(dist: Int) = OriginRelative(x * dist, y * dist, z * dist)

