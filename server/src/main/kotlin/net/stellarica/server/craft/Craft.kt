package net.stellarica.server.craft

import io.papermc.paper.entity.TeleportFlag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.audience.ForwardingAudience
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import net.stellarica.common.util.OriginRelative
import net.stellarica.common.util.asDegrees
import net.stellarica.common.util.rotate
import net.stellarica.common.util.rotateCoordinates
import net.stellarica.common.util.toBlockPos
import net.stellarica.common.util.toVec3
import net.stellarica.server.craft.starship.Starship
import net.stellarica.server.multiblock.MultiblockHandler
import net.stellarica.server.multiblock.MultiblockInstance
import net.stellarica.server.util.extension.bukkit
import net.stellarica.server.util.extension.sendRichMessage
import net.stellarica.server.util.extension.toLocation
import net.stellarica.server.util.extension.toVec3
import org.bukkit.Chunk
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import java.util.concurrent.ConcurrentHashMap
import kotlin.system.measureTimeMillis

/**
 * Base class for all Crafts; sets of moving blocks
 */
open class Craft(
	/**
	 * The point from which detection starts, and
	 * the craft rotates around
	 */
	var origin: BlockPos,
	var direction: Direction,
	var world: ServerLevel,
	var owner: Player? = null
) : ForwardingAudience {

	companion object {
		const val sizeLimit = 500000
	}

	var detectedBlocks = mutableSetOf<BlockPos>()

	var passengers = mutableSetOf<Entity>()

	val detectedBlockCount: Int
		get() = detectedBlocks.size

	var multiblocks = mutableSetOf<OriginRelative>()


	/**
	 * Holds the min and max relative Y values for each column in the ship
	 * Can be used to check if a certain block is "inside" though not neccecarily detected
	 * @see contains
	 */
	var contents = mutableMapOf<RelativeColumn, Pair<Int, Int>>()


	/**
	 * @return Whether [block] is considered to be inside this craft
	 */
	fun contains(block: BlockPos?): Boolean {
		block ?: return false
		if (detectedBlocks.contains(block)) return true
		val rel = OriginRelative.getOriginRelative(
			block,
			origin,
			direction
		)
		val extremes = contents[RelativeColumn(rel)] ?: return false
		return extremes.first >= rel.y && extremes.second <= rel.y
	}

	private fun calculateContents() {
		contents.clear()
		detectedBlocks.forEach { pos ->
			// might cause issues if run when direction isn't north?
			// juuust to be safe...
			if (direction != Direction.NORTH) throw IllegalStateException("Direction is not north")

			val block = OriginRelative.getOriginRelative(pos, origin, direction)
			val extremes = contents.getOrPut(RelativeColumn(block)) { Pair(block.y, block.y) }

			if (block.y < extremes.first) {
				contents[RelativeColumn(block)] = Pair(block.y, extremes.second)
			} else if (block.y > extremes.second) {
				contents[RelativeColumn(block)] = Pair(extremes.first, block.y)
			}
		}
	}

	fun getMultiblock(pos: OriginRelative): MultiblockInstance? {
		val mb = pos.getBlockPos(origin, direction)
		return MultiblockHandler[world.getChunkAt(mb).bukkit].firstOrNull { it.origin == mb }
	}

	/**
	 * Translate the craft by [offset] blocks
	 * @see change
	 */
	fun move(offset: Vec3i) {
		val change = offset.toVec3()
		// don't want to let them pass a vec3
		// since the ships snap to blocks but entities can actually move by that much
		// relative entity teleportation will be messed up

		change({ current ->
			return@change current.add(change)
		}, world)
	}

	/**
	 * Rotate the craft and contents by [rotation]
	 * @see change
	 */
	fun rotate(rotation: Rotation) {
		change({ current ->
			return@change rotateCoordinates(current, origin.toVec3(), rotation)
		}, world, rotation)
	}

	private fun change(
		/** The transformation to apply to each block in the craft */
		modifier: (Vec3) -> Vec3,
		/** The world to move to */
		targetWorld: ServerLevel,
		/** The amount to rotate each directional block by */
		rotation: Rotation = Rotation.NONE,
		/** Callback called after the craft finishes moving */
		callback: () -> Unit = {}
	) {
		// calculate new blocks locations
		val targetsCHM = ConcurrentHashMap<BlockPos, BlockPos>()

		runBlocking {
			for (section in detectedBlocks.chunked(detectedBlocks.size / 8 + 256)) {
				// chunk into sections to process parallel
				launch(Dispatchers.Default) {
					val new = section.zip(section.map { current -> modifier(current.toVec3()).toBlockPos() })
					targetsCHM.putAll(new)
				}
			}
		}

		// possible slight optimization because iterating over a CHM is pain
		// tested it, but not very thoroughly. Converting to a map *is* slow and takes time
		val targets = targetsCHM.toMap()

		// We need to get the original blockstates before we start setting blocks
		// otherwise, if we just try to get the state as we set the blocks, the state might have already been set.
		// Consider moving a blocks from b to c. If a has already been moved to b, we don't want to copy a to c.
		// see https://discord.com/channels/1038493335679156425/1038504764356427877/1066184457264046170
		//
		// However, we don't need to go and get the states of the current blocks, as if it isn't in
		// the target blocks, it won't be overwritten, so we can just get it when it comes time to set the blocks
		//
		// This solution ~~may not be~~ isn't the most efficient, but it works
		val original = mutableMapOf<BlockPos, BlockState>()
		val entities = mutableMapOf<BlockPos, BlockEntity>()

		// check for collisions
		// if the world we're moving to isn't the world we're coming from, the whole map of original states we got is useless
		val sameWorld = world == targetWorld
		targets.values.forEach { target ->
			val state = targetWorld.getBlockState(target)

			if (!state.isAir && !(sameWorld && detectedBlocks.contains(target))) {
				sendRichMessage("<gold>Blocked by ${world.getBlockState(target).block.name} at <bold>(${target.x}, ${target.y}, ${target.z}</bold>)!\"")
				return
			}

			// also use this time to get the original state of these blocks
			if (state.hasBlockEntity()) entities[target] = targetWorld.getBlockEntity(target)!!

			original[target] = state
		}

		// iterating over twice isn't great, maybe there's a way to condense it?
		val newDetectedBlocks = mutableSetOf<BlockPos>()
		targets.forEach { (current, target) ->
			val currentBlock = original.getOrElse(current) { world.getBlockState(current) }

			// set the blocks
			targetWorld.setBlockFast(target, currentBlock.rotate(rotation))
			newDetectedBlocks.add(target)

			// move any entities
			if (entities.contains(current) || currentBlock.hasBlockEntity()) {
				val entity = entities.getOrElse(current) { world.getBlockEntity(current)!! }

				world.getChunk(current).removeBlockEntity(current)

				entity.blockPos = target
				entity.level = targetWorld
				entity.setChanged()

				targetWorld.getChunk(target).setBlockEntity(entity)
			}
		}

		// if this ever happens its a really good sign something died lol
		if (newDetectedBlocks.size != detectedBlocks.size)
			println("Lost ${detectedBlocks.size - newDetectedBlocks.size} blocks while moving! This is a bug!")

		// set air where we were
		if (world == targetWorld) detectedBlocks.removeAll(newDetectedBlocks)
		detectedBlocks.forEach { world.setBlockFast(it, Blocks.AIR.defaultBlockState()) }

		detectedBlocks = newDetectedBlocks

		// move multiblocks, and remove any that no longer exist (i.e. were destroyed)
		val mbs = mutableSetOf<MultiblockInstance>()
		multiblocks.removeIf { pos -> getMultiblock(pos)?.also { mbs.add(it) } == null }
		for (mb in mbs) {
			val new = MultiblockInstance(
				mb.id,
				modifier(mb.origin.toVec3()).toBlockPos(),
				targetWorld.world,
				mb.direction.rotate(rotation),
				mb.type,
				mb.data
			)
			MultiblockHandler[mb.chunk].remove(mb)
			MultiblockHandler[targetWorld.getChunkAt(new.origin).bukkit].add(new)
		}

		// move pipe networks
		/*
		for (net in PipeHandler.activeNetworks[world.world]!!.filter { it.origin in detectedBlocks }) {
			net.origin = modifier(net.origin.toVec3()).toBlockPos()
			net.direction = net.direction.rotate(rotation)
			net.world = targetWorld.world
			PipeHandler.activeNetworks.getOrPut(targetWorld.world){ mutableSetOf() }.add(net)
		}
		 */


		// finish up
		movePassengers(modifier, rotation)
		world = targetWorld
		origin = modifier(origin.toVec3()).toBlockPos()
		direction = direction.rotate(rotation)
		callback()
	}


	fun detect() {
		var nextBlocksToCheck = detectedBlocks
		nextBlocksToCheck.add(origin)
		detectedBlocks = mutableSetOf()
		val checkedBlocks = nextBlocksToCheck.toMutableSet()

		val startTime = System.currentTimeMillis()

		val chunks = mutableSetOf<Chunk>()

		while (nextBlocksToCheck.size > 0) {
			val blocksToCheck = nextBlocksToCheck
			nextBlocksToCheck = mutableSetOf()

			for (currentBlock in blocksToCheck) {

				// todo: block tags for detection? this is bad
				if (world.getBlockState(currentBlock).isAir) continue

				if (detectedBlocks.size > sizeLimit) {
					owner?.sendRichMessage("<gold>Detection limit reached. (${sizeLimit} blocks)")
					nextBlocksToCheck.clear()
					detectedBlocks.clear()
					break
				}

				detectedBlocks.add(currentBlock)
				chunks.add(world.getChunkAt(currentBlock).bukkit)

				// Slightly condensed from MSP's nonsense, but this could be improved
				for (x in listOf(-1, 1)) {
					val block = currentBlock.offset(x, 0, 0)
					if (!checkedBlocks.contains(block)) {
						nextBlocksToCheck.add(block)
					}
				}
				for (z in listOf(-1, 1)) {
					val block = currentBlock.offset(0, 0, z)
					if (!checkedBlocks.contains(block)) {
						nextBlocksToCheck.add(block)
					}
				}
				for (y in -1..1) {
					val block = currentBlock.offset(0, y, 0)
					if (!checkedBlocks.contains(block)) {
						checkedBlocks.add(block)
						nextBlocksToCheck.add(block)
					}
				}
			}
		}

		val elapsed = System.currentTimeMillis() - startTime
		owner?.sendRichMessage("<green>Craft detected! (${detectedBlocks.size} blocks)")
		owner?.sendRichMessage(
			"<gray>Detected ${detectedBlocks.size} blocks in ${elapsed}ms. " +
					"(${detectedBlocks.size / elapsed.coerceAtLeast(1)} blocks/ms)"
		)
		owner?.sendRichMessage(
			"<gray>Calculated Contents in ${
				measureTimeMillis {
					calculateContents()
				}
			}ms.")

		// Detect all multiblocks
		multiblocks.clear()
		// this is probably slow
		multiblocks.addAll(chunks
			.map { MultiblockHandler[it] }
			.flatten()
			.filter { detectedBlocks.contains(it.origin) }
			.map { OriginRelative.getOriginRelative(it.origin, origin, direction) }
		)

		owner?.sendRichMessage("<gray>Detected ${multiblocks.size} multiblocks")
	}

	@Suppress("UnstableApiUsage")
	fun movePassengers(offset: (Vec3) -> Vec3, rotation: Rotation = Rotation.NONE) {
		for (passenger in passengers) {
			// TODO: FIX
			// this is not a good solution because if there is any rotation, the player will not be translated by the offset
			// The result is that any ship movement that attempts to rotate and move in the same action will break.
			// For now there aren't any actions like that, but if there are in the future, this will need to be fixed.
			//
			// Rotating the whole ship around the adjusted origin will not work,
			// as rotating the ship 4 times does not bring it back to the original position
			//
			// However, without this dumb fix players do not rotate to the proper relative location
			val destination =
				if (rotation != Rotation.NONE) rotateCoordinates(
					passenger.location.toVec3(),
					origin.toVec3().add(
						Vec3(
							0.5,
							0.0,
							0.5
						)
					), rotation
				).toLocation(world.world)
				else offset(passenger.location.toVec3()).toLocation(world.world)


			destination.world = passenger.world // todo: fix

			destination.pitch = passenger.location.pitch
			destination.yaw = (passenger.location.yaw + rotation.asDegrees).toFloat()

			passenger.teleport(
				destination,
				PlayerTeleportEvent.TeleportCause.PLUGIN,
				TeleportFlag.EntityState.RETAIN_OPEN_INVENTORY, // this might cause issues...
				*TeleportFlag.Relative.values()
			)
		}
	}

	fun messagePilot(message: String) {
		if (this is Starship) {
			pilot?.sendRichMessage(message) ?: owner?.sendRichMessage(message)
		}
	}

	data class RelativeColumn(val x: Int, val z: Int) {
		constructor(pos: OriginRelative) : this(pos.x, pos.z)
	}

	override fun audiences() = passengers
}
