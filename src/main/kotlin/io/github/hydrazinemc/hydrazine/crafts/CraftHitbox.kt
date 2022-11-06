package io.github.hydrazinemc.hydrazine.crafts

import io.github.hydrazinemc.hydrazine.utils.OriginRelative

class CraftHitbox {

	// Every position in the hitbox
	private var bounds = mutableSetOf<OriginRelative>()

	fun calculate(blocks: Set<OriginRelative>) {
		// for each block
		// if there is no other block with the same x and z, add this one
		// if there is, add this one and all blocks with ys between

		blocks.forEach {block ->
			val max = bounds.filter { it.x == block.x && it.z == block.z }.maxByOrNull { it.y }?.y ?: block.y
			for (y in block.y..max) {
				bounds.add(OriginRelative(block.x, y, block.z))
			}
		}
	}

	fun contains(loc: OriginRelative): Boolean {
		return bounds.contains(loc)
	}
}