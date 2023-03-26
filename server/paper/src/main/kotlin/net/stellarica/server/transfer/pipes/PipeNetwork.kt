package net.stellarica.server.transfer.pipes

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.Blocks
import net.stellarica.common.utils.OriginRelative
import net.stellarica.server.transfer.NodeNetwork

class PipeNetwork(
	val origin: BlockPos,
	val world: ServerLevel
) : NodeNetwork<Fuel>() {
	var direction = Direction.NORTH

	fun detect() {
		nodes.clear()
		val start = System.currentTimeMillis()

		val undirectedNodes = mutableSetOf<Set<OriginRelative>>()
		val inputsPositions = mutableSetOf<OriginRelative>()
		println("Starting node detection")
		detectConnectedPairs(OriginRelative(0,0,0), undirectedNodes, mutableSetOf(OriginRelative(0,0,0)), inputsPositions)
		println("Found undirected nodes:\n${
			undirectedNodes.joinToString("\n") {
				"${it.first()} <-> ${it.last()}".replace(
					"OriginRelative",
					""
				)
			}
		}")
		println("Elapsed time ${System.currentTimeMillis() - start}ms")
	}

	private fun detectConnectedPairs(
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
				for (dist in 1..50) {
					val next = pos + rel * dist
					if (isRod(next)) continue
					if (isCopper(next)) {
						if (next !in detected) {
							if (isInput(next)) inputs.add(next)
							detected.add(next)
							detectConnectedPairs(next, nodes, detected, inputs)
						}
						nodes.add(setOf(pos, next))
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

	override fun tick() {
		TODO("Not yet implemented")
	}
}

private operator fun OriginRelative.plus(other: OriginRelative) = OriginRelative(x + other.x, y + other.y, z + other.z)
private operator fun OriginRelative.times(dist: Int) = OriginRelative(x * dist, y * dist, z * dist)

