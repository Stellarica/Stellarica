package net.stellarica.server.craft

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.stellarica.common.coordinate.BlockPosition
import net.stellarica.common.coordinate.RelativeBlockPosition
import net.stellarica.common.util.rotate
import net.stellarica.common.util.toBlockPos
import net.stellarica.server.StellaricaServer.Companion.klogger
import net.stellarica.server.multiblock.MultiblockInstance
import java.util.concurrent.ConcurrentHashMap

abstract class BasicCraft : Craft, MultiblockContainer {

	final override lateinit var origin: BlockPosition
	final override lateinit var orientation: Direction
	final override lateinit var world: ServerLevel
		protected set

	// You might think a list would be better, but we do so many .contains() checks
	// that an ArrayList is orders of magnitude slower. LinkedHashSet doesn't have
	// that bad of iteration time anyway.
	protected var detectedBlocks = mutableSetOf<BlockPosition>()

	protected val multiblocks = mutableSetOf<RelativeBlockPosition>()

	override val blockCount: Int
		get() = detectedBlocks.size

	override fun contains(block: BlockPosition): Boolean {
		return detectedBlocks.contains(block) // slow! (because list)
	}

	override fun getMultiblockAt(pos: RelativeBlockPosition): MultiblockInstance? {
		TODO()
	}

	override fun addMultiblock(multiblock: MultiblockInstance) {
		multiblocks.add(this.getRelativePos(multiblock.origin))
	}

	override fun removeMultiblock(multiblock: MultiblockInstance): Boolean {
		return multiblocks.remove(this.getRelativePos(multiblock.origin))
	}

	fun checkTransformation(transformation: CraftTransformation): CraftCheckResult {
		val targets = calcNewCoords(transformation)
		// We need to get the original blockstates before we start setting blocks
		// otherwise, if we just try to get the state as we set the blocks, the state might have already been set.
		// Consider moving a blocks from b to c. If a has already been moved to b, we don't want to copy a to c.
		// see https://discord.com/channels/1038493335679156425/1038504764356427877/1066184457264046170
		//
		// However, we don't need to go and get the states of the current blocks, as if it isn't in
		// the target blocks, it won't be overwritten, so we can just get it when it comes time to set the blocks
		//
		// This solution ~~may not be~~ isn't the most efficient, but it works
		val original = mutableMapOf<BlockPosition, BlockState>()
		val entities = mutableMapOf<BlockPosition, BlockEntity>()

		// check for collisions
		// if the world we're moving to isn't the world we're coming from, the whole map of original states we got is useless
		val sameWorld = world == transformation.world
		for (target in targets.values) {
			val targetBlockPos = target.toBlockPos() // pain
			val state = transformation.world.getBlockState(targetBlockPos)

			if (!state.isAir && !(sameWorld && detectedBlocks.contains(target))) {
				return CraftCheckResult(true, null)
			}

			// also use this time to get the original state of these blocks
			if (state.hasBlockEntity()) entities[target] = transformation.world.getBlockEntity(targetBlockPos)!!

			original[target] = state
		}
		return CraftCheckResult(false, PartialMoveData(targets, original, entities))
	}

	override fun transform(transformation: CraftTransformation): Boolean {
		val res = checkTransformation(transformation)
		if (res.collision) {
			return false
		}
		moveBlocks(transformation, res.data!!)
		moveMultiblocks(transformation)
		return true
	}

	protected fun moveBlocks(transformation: CraftTransformation, data: PartialMoveData) {

		// iterating over twice isn't great, maybe there's a way to condense it?
		val newDetectedBlocks = mutableSetOf<BlockPosition>()
		for ((current, target) in data.targets) {
			val currentPos = current.toBlockPos()
			val targetPos = target.toBlockPos()
			val currentBlock = data.original.getOrElse(current) { world.getBlockState(currentPos) }

			// set the blocks
			transformation.world.setBlockFast(targetPos, currentBlock.rotate(transformation.rotation))
			newDetectedBlocks.add(target)

			// move any entities
			if (data.entities.contains(current) || currentBlock.hasBlockEntity()) {
				val entity = data.entities.getOrElse(current) { world.getBlockEntity(currentPos)!! }

				world.getChunk(currentPos).removeBlockEntity(currentPos)

				entity.blockPos = targetPos
				entity.level = transformation.world
				entity.setChanged()


				transformation.world.getChunk(targetPos).setBlockEntity(entity)
			}
		}

		// if this ever happens it's a really good sign something died lol
		if (newDetectedBlocks.size != detectedBlocks.size)
			klogger.error { "Lost ${detectedBlocks.size - newDetectedBlocks.size} blocks while moving! This is a bug!" }

		// set air where we were
		if (world == transformation.world) detectedBlocks.removeAll(newDetectedBlocks)
		detectedBlocks.forEach { world.setBlockFast(it.toBlockPos(), Blocks.AIR.defaultBlockState()) }


		this.world = transformation.world
		detectedBlocks = newDetectedBlocks
		origin = transformation.offset(origin)
		this.orientation = this.orientation.rotate(transformation.rotation)
	}

	protected fun moveMultiblocks(transformation: CraftTransformation) {
		// todo
	}

	private fun calcNewCoords(transformation: CraftTransformation): Map<BlockPosition, BlockPosition> {
		// calculate new blocks locations
		val targetsCHM = ConcurrentHashMap<BlockPosition, BlockPosition>()

		runBlocking {
			for (section in detectedBlocks.chunked(detectedBlocks.size / 8)) {
				// chunk into sections to process parallel
				launch(Dispatchers.Default) {
					val new = section.zip(section.map { current -> transformation.offset(current) })
					targetsCHM.putAll(new)
				}
			}
		}
		return targetsCHM
	}

	class PartialMoveData(
			val targets: Map<BlockPosition, BlockPosition>,
			val original: Map<BlockPosition, BlockState>,
			val entities: Map<BlockPosition, BlockEntity>
	)

	class CraftCheckResult(
			val collision: Boolean,
			val data: PartialMoveData?
	)
}