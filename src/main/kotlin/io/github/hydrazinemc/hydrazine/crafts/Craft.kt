package io.github.hydrazinemc.hydrazine.crafts

import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.klogger
import io.github.hydrazinemc.hydrazine.crafts.pilotable.Pilotable
import io.github.hydrazinemc.hydrazine.multiblocks.MultiblockInstance
import io.github.hydrazinemc.hydrazine.multiblocks.Multiblocks
import io.github.hydrazinemc.hydrazine.utils.AlreadyMovingException
import io.github.hydrazinemc.hydrazine.utils.ConfigurableValues
import io.github.hydrazinemc.hydrazine.utils.OriginRelative
import io.github.hydrazinemc.hydrazine.utils.Tasks
import io.github.hydrazinemc.hydrazine.utils.Vector3
import io.github.hydrazinemc.hydrazine.utils.locations.BlockLocation
import io.github.hydrazinemc.hydrazine.utils.locations.ChunkLocation
import io.github.hydrazinemc.hydrazine.utils.nms.removeBlockEntity
import io.github.hydrazinemc.hydrazine.utils.nms.setBlockEntity
import io.github.hydrazinemc.hydrazine.utils.nms.setBlockFast
import io.github.hydrazinemc.hydrazine.utils.nms.tileEntities
import io.github.hydrazinemc.hydrazine.utils.rotation.RotationAmount
import io.github.hydrazinemc.hydrazine.utils.rotation.rotate
import io.github.hydrazinemc.hydrazine.utils.rotation.rotateBlockFace
import io.github.hydrazinemc.hydrazine.utils.rotation.rotateCoordinates
import io.papermc.paper.entity.RelativeTeleportFlag
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import org.bukkit.Bukkit
import org.bukkit.ChunkSnapshot
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.data.BlockData
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import kotlin.math.pow
import kotlin.system.measureTimeMillis

/**
 * Base class for all Crafts; sets of moving blocks
 */
open class Craft(
	/**
	 * The point from which detection starts, and
	 * the craft rotates around
	 */
	var origin: BlockLocation
) {

	private var detectedBlocks = mutableSetOf<BlockLocation>()
	var multiblocks = mutableSetOf<MultiblockInstance>()
	private var chunkCache = mutableMapOf<ChunkLocation, ChunkSnapshot>()

	/**
	 * Whether the ship is currently moving.
	 * @see lastMoved
	 */
	var isMoving = false

	/**
	 * The time (in millis) that this craft took to move
	 * when it was last moved.
	 *
	 * Note that this does not include time spent queueing movement,
	 * but only the time to place the blocks.
	 *
	 * Handled by [CraftBlockSetter]
	 */
	var timeSpentMoving: Long = 0

	/**
	 * The.. uh.. passengers...
	 */
	var passengers = mutableSetOf<Entity>()

	/**
	 * Remnant from MSP, I honestly have no idea, and am
	 * too lazy to figure it out
	 */
	var allowedBlocks = mutableSetOf<Material>()

	/**
	 * The blocks that this ship cannot contain
	 */
	var undetectables = mutableSetOf<Material>()

	/**
	 * The number of detected blocks
	 */
	val blockCount: Int
		get() = detectedBlocks.size


	/**
	 * The blocks considered to be "inside" of the ship, but not neccecarily detected.
	 */
	private var bounds = mutableSetOf<OriginRelative>()

	/**
	 * Message this craft's pilot, if it has one.
	 * If the ship isn't being piloted, message the owner.
	 * MiniMessage formatting is allowed
	 *
	 * @see messagePassengers
	 */
	fun messagePilot(message: String) {
		if (this is Pilotable) {
			pilot?.sendRichMessage(message) ?: owner?.sendRichMessage(message)
		}
	}

	/**
	 * Message all passengers of this craft.
	 * MiniMessage formatting is allowed
	 *
	 * @see messagePilot
	 */
	fun messagePassengers(message: String) {
		passengers.forEach { it.sendRichMessage(message) }
	}

	/**
	 * @return Whether [block] is considered to be inside this craft
	 */
	fun contains(block: BlockLocation?): Boolean {
		block ?: return false
		return detectedBlocks.contains(block) || bounds.contains((block - origin).let {OriginRelative(it.x, it.y, it.z)})
	}

	/**
	 * @return Whether [loc] is considered to be inside this craft
	 */
	fun contains(loc: Location?): Boolean {
		loc ?: return false
		return contains(BlockLocation(loc))
	}


	fun calculateHitbox() {
		detectedBlocks.map {pos -> (pos - origin).let {OriginRelative(it.x, it.y, it.z)}}.sortedBy { -it.y }.forEach {block ->
			val max = bounds.filter { it.x == block.x && it.z == block.z }.maxByOrNull { it.y }?.y ?: block.y
			for (y in block.y..max) {
				bounds.add(OriginRelative(block.x, y, block.z))
			}
		}
	}


	/**
	 * Detect this craft
	 * @throws AlreadyMovingException if the craft is moving
	 */
	fun detect() {
		if (isMoving) throw AlreadyMovingException("Craft attempted to detect, but is currently moving!")
		messagePilot("<gray>Detecting craft...")
		Tasks.async {
			chunkCache.clear()
			val time = measureTimeMillis {

				var nextBlocksToCheck = detectedBlocks
				nextBlocksToCheck.add(origin)
				detectedBlocks = mutableSetOf()
				val checkedBlocks = nextBlocksToCheck.toMutableSet()

				updateUndetectables()

				while (nextBlocksToCheck.size > 0) {
					val blocksToCheck = nextBlocksToCheck
					nextBlocksToCheck = mutableSetOf()

					for (currentBlock in blocksToCheck) {
						val type = getCachedBlockData(currentBlock).material

						if (undetectables.contains(type)) continue

						if (detectedBlocks.size > ConfigurableValues.detectionLimit) {
							klogger.info { "Detection limit reached. (${ConfigurableValues.detectionLimit})" }
							messagePilot("<gold>Detection limit reached. (${ConfigurableValues.detectionLimit} blocks)")
							nextBlocksToCheck.clear()
							detectedBlocks.clear()
							break
						}

						detectedBlocks.add(currentBlock)

						// Slightly condensed from MSP's nonsense, but this could be improved
						for (x in -1..1) {
							for (y in -1..1) {
								for (z in -1..1) {
									if (x == y && z == y && y == 0) continue
									val block = currentBlock + BlockLocation(x, y, z, currentBlock.world)
									if (!checkedBlocks.contains(block)) {
										checkedBlocks.add(block)
										nextBlocksToCheck.add(block)
									}
								}
							}
						}
					}
				}
			}
			messagePilot("<green>Craft detected! (${detectedBlocks.size} blocks)")
			messagePilot(
				"<gray>Detected ${detectedBlocks.size} blocks in ${time}ms. " +
						"(${detectedBlocks.size / time.coerceAtLeast(1)} blocks/ms)"
			)
			messagePilot("<gray>Calculated Hitbox in ${measureTimeMillis { 
				calculateHitbox()
			}
			}ms. (${bounds.size} blocks)")

			// Detect all multiblocks
			multiblocks.clear()
			// this is probably slow
			multiblocks.addAll(Multiblocks.activeMultiblocks.filter { detectedBlocks.contains(BlockLocation(it.origin)) })

			messagePilot("<gray>Detected ${multiblocks.size} multiblocks")
			chunkCache.clear()
		}
	}

	/**
	 * Move all passengers by offset.
	 * Uses bukkit to teleport entities, and NMS to move players.
	 */
	fun movePassengers(offset: (Vector3) -> Vector3, rotation: RotationAmount = RotationAmount.NONE) {
		passengers.forEach {
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
				if (rotation != RotationAmount.NONE) Vector3(it.location).rotateAround(
					Vector3(origin) + Vector3(
						0.5,
						0.0,
						0.5
					), rotation
				).asLocation
				else offset(Vector3(it.location)).asLocation


			destination.world = it.world // todo: fix

			destination.pitch = it.location.pitch
			destination.yaw = it.location.yaw + rotation.asDegrees

			if (it is Player) it.teleport(
				destination,
				PlayerTeleportEvent.TeleportCause.PLUGIN,
				false,
				true,
				*RelativeTeleportFlag.values(),
			)
			else it.teleport(destination)
		}
	}


	/**
	 * Update this craft's undetectables.
	 */
	fun updateUndetectables() {
		// Construct the undetectable list
		undetectables =
			ConfigurableValues.defaultUndetectable.toMutableSet() // Get a copy of all default undetectables
		undetectables.addAll(ConfigurableValues.forcedUndetectable)               // Add all forced undetectables
		undetectables.removeAll(allowedBlocks)
		messagePilot("<gray>Updated craft's undetectable blocks.")
	}

	/**
	 * Translate the craft by [offset] blocks
	 * @throws AlreadyMovingException if craft movement is currently queued.
	 * @see queueChange
	 */
	fun queueMovement(offset: BlockLocation) {
		queueChange({ current ->
			return@queueChange current + Vector3(offset)
		}, "Movement", origin.world!!)
	}

	/**
	 * Rotate the craft and contents by [rotation]
	 * @throws AlreadyMovingException if craft movement is currently queued.
	 * @see queueChange
	 */
	fun queueRotation(rotation: RotationAmount) {
		queueChange({ current ->
			return@queueChange rotateCoordinates(current, Vector3(origin), rotation)
		}, "Rotation", origin.world!!, rotation)
		Tasks.async {
			calculateHitbox() // rather than keep track of a hitbox rotation, just recacluate it when we rotate.
		}
	}

	/**
	 * Queue a starships move in [StarshipBlockSetter].
	 * Asynchronously calculates where the ship needs to move to, and queues
	 * block placements in [blockSetQueueQueue].
	 *
	 * @param modifier the function through which block coordinates are passed
	 * @param name the name of the change (e.g "Rotation" or "Translation")
	 * @param world the world in which to place the target blocks
	 * @param rotation the rotation applied. Not used to move blocks, but rotates entities and rotational blocks
	 * @see queueMovement
	 * @see queueWorldChange
	 * @see queueRotation
	 * @throws AlreadyMovingException if this ship already has movement queued.
	 */
	private fun queueChange(
		modifier: (Vector3) -> Vector3,
		name: String,
		world: World,
		rotation: RotationAmount = RotationAmount.NONE
	) {
		if (isMoving) throw AlreadyMovingException("Craft attempted to queue movement, but it is already moving!")
		isMoving = true

		// While async, calculate the target blocks
		Tasks.async {
			chunkCache.clear()

			val newDetectedBlocks = mutableSetOf<BlockLocation>()
			val blocksToSet = mutableMapOf<BlockLocation, BlockData>()
			val airData = Bukkit.createBlockData(Material.AIR)
			val entities = mutableMapOf<BlockLocation, BlockLocation>()

			detectedBlocks.forEach { currentBlockLocation ->
				val currentBlockData = getCachedBlockData(currentBlockLocation)
				val targetBlockLocation =
					modifier(Vector3(currentBlockLocation)).asBlockLocation.apply { this.world = world }

				blocksToSet.putIfAbsent(currentBlockLocation, airData)

				currentBlockData.rotate(rotation)
				blocksToSet[targetBlockLocation] = currentBlockData

				// This is really, really, really, really, stupid.
				// Sadly afaik there's no way to get a tile entity from a chunk snapshot
				// and the benefits of doing this async outweigh this pain and suffering
				if (currentBlockData.material in tileEntities) entities[currentBlockLocation] = targetBlockLocation

				// Step 5: Add the target block to the new detected blocks list.
				if (!newDetectedBlocks.add(targetBlockLocation)) klogger.warn {
					"A newly detected block was overwritten while queueing $name!"
				}
			}

			if (detectedBlocks.size != newDetectedBlocks.size) {
				messagePassengers(
					"<red>Lost <bold>${detectedBlocks.size - newDetectedBlocks.size}</bold> " +
							"blocks while queuing $name!"
				)
				messagePassengers("<bold><gold>This is a bug, please report it.")
			}
			chunkCache.clear()

			// On the next server tick, check for collisions and move the ship
			Tasks.sync {
				val timeSpent = measureTimeMillis {

					// Check for collisions
					blocksToSet.forEach {
						// If there is a non-air block there, cancel the move
						if (!detectedBlocks.contains(it.key) && !it.key.bukkit.type.isAir) {
							// The ship is blocked!
							messagePilot(
								"<gold>$name blocked by ${it.key.bukkit.type} at " +
										"<bold>(${it.key.x}, ${it.key.y}, ${it.key.z}</bold>)!"
							)
							this.isMoving = false
							chunkCache.clear()
							return@sync
						}
					}

					// no collisions, move the ship
					movePassengers(modifier, rotation)

					// get nms tile entities, and remove them
					val e = mutableMapOf<BlockEntity, Pair<Level, BlockPos>>() // pair is target
					entities.forEach {
						val world = (world as CraftWorld).handle
						e[removeBlockEntity(world, it.key.asBlockPos) ?: return@forEach] =
							Pair(world, it.value.asBlockPos)
					}

					// move blocks
					blocksToSet.forEach {
						val loc = it.key.asLocation
						setBlockFast(loc, it.value)
					}

					// use sendMultiBlockChange to reduce visual artifacts
					Bukkit.getServer().onlinePlayers.forEach { player ->
						if (
							Vector3(player.location).distanceSquared(Vector3(origin)) <
							(Bukkit.getServer().viewDistance * 16.0).pow(2)
						)
						// if the player can see the craft, send the change
						player.sendMultiBlockChange(blocksToSet.mapKeys { it.key.asLocation }, true)
					}

					// TODO: update heightmaps

					// set entities back
					e.forEach { (entity, pos) ->
						setBlockEntity(pos.first, pos.second, entity)
					}

					// move multiblocks
					multiblocks.forEach { multiblock ->
						// Figure out where to go
						val oldLoc = multiblock.origin.clone()
						val newLoc =
							modifier(Vector3(oldLoc)).asLocation.apply { this@apply.world = world }

						// Update the multiblock itself
						multiblock.origin = newLoc
						multiblock.facing = rotateBlockFace(multiblock.facing, rotation)
					}

					// let the craft know we're done here
					origin = modifier(Vector3(origin)).asBlockLocation.apply { this.world = world }
					isMoving = false
					detectedBlocks = newDetectedBlocks

				}
				timeSpentMoving = timeSpent
			}
		}
	}

	private fun getCachedBlockData(block: BlockLocation): BlockData {
		val chunkCoord = ChunkLocation(block.x shr 4, block.z shr 4)
		val currentChunk = chunkCache.getOrPut(chunkCoord) {
			block.world!!.getChunkAt(
				chunkCoord.x,
				chunkCoord.z
			).getChunkSnapshot(false, false, false)
		}
		return currentChunk.getBlockData(
			block.x - (chunkCoord.x shl 4),
			block.y,
			block.z - (chunkCoord.z shl 4)
		)
	}
}
