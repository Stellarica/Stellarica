package net.stellarica.server.multiblock

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import net.minecraft.core.Direction
import net.stellarica.common.coordinate.BlockPosition
import net.stellarica.server.Multiblocks
import net.stellarica.server.StellaricaServer
import net.stellarica.server.material.item.custom.DebugCustomItems
import net.stellarica.server.material.item.type.ItemType
import net.stellarica.server.util.Tasks
import net.stellarica.server.util.extension.toBlockPosition
import net.stellarica.server.util.extension.toLocation
import net.stellarica.server.util.extension.toNamespacedKey
import net.stellarica.server.util.extension.vanilla
import net.stellarica.server.util.sendRichActionBar
import net.stellarica.server.util.wrapper.ServerWorld
import org.bukkit.Chunk
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.persistence.PersistentDataType
import java.util.UUID

object MultiblockHandler : Listener {
	/*
	Hey, hey you, do you want to see something cursed?
	Be glad I didn't end up using this:

	private val multiblocks = object : MutableMap<Chunk, MutableSet<MultiblockInstance>> by mutableMapOf() {
		override operator fun get(key: Chunk): MutableSet<MultiblockInstance> = getOrPut(key) { mutableSetOf() }
	}
	*/
	private val multiblocks = mutableMapOf<Chunk, MutableSet<MultiblockInstance>>()


	init {
		Tasks.syncRepeat(5, 20) {
			removeInvalidMultiblocks()
		}
	}

	private fun removeInvalidMultiblocks() {

		for ((_, mbSet) in multiblocks) {
			val invalid = mutableSetOf<MultiblockInstance>()
			for (multiblock in mbSet) {
				if (!multiblock.validate()) {
					invalid.add(multiblock)
				}
			}
			mbSet.removeAll(invalid)
		}
	}

	/**
	 * @return the multiblock with origin at [pos], or null if none exists
	 */
	fun getMultiblockAt(pos: BlockPosition, world: ServerWorld): MultiblockInstance? {
		return multiblocks[world.getChunkAt(pos)]?.firstOrNull { it.origin == pos }
	}

	/**
	 * @return the multiblock containing [pos] or null if none exists
	 */
	fun getMultiblocksContaining(pos: BlockPosition, world: ServerWorld): Collection<MultiblockInstance> {
		val neighbors = arrayOf(
			world.getChunkAt(pos),
			world.getChunkAt(pos + BlockPosition(0, 0, 16)),
			world.getChunkAt(pos + BlockPosition(0, 0, -16)),
			world.getChunkAt(pos + BlockPosition(16, 0, 0)),
			world.getChunkAt(pos + BlockPosition(-16, 0, 0)),
		)

		val found = mutableSetOf<MultiblockInstance>()
		for (chunk in neighbors) {
			multiblocks[chunk]?.let { set ->
				found.addAll(set.filter { it.contains(pos) })
			}
		}

		return found
	}

	fun getAllLoaded(): Collection<MultiblockInstance> {
		return multiblocks.values.flatten()
	}

	/**
	 * Moves [multiblock] to [newOrigin] in [newWorld]
	 * Note that this does not change any blocks, or update the multiblock instance itself.
	 * It merely updates the multiblock lookup, so be sure to set blocks and modify the MultiblockInstance as needed.
	 */
	fun moveMultiblock(multiblock: MultiblockInstance, newOrigin: BlockPosition, newWorld: ServerWorld) {
		assert(multiblocks[multiblock.world.getChunkAt(multiblock.origin)]!!.remove(multiblock))
		assert(multiblocks.getOrPut(newWorld.getChunkAt(newOrigin)){ mutableSetOf() }.add(multiblock))
	}


	fun tryDetect(origin: BlockPosition, world: ServerWorld): MultiblockInstance? {
		getMultiblockAt(origin, world)?.let { return it } // overlapping origins would be cringe

		val possible = mutableListOf<MultiblockInstance>()
		for (type in Multiblocks) {
			for (facing in setOf(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST)) {
				if (type.validatePattern(facing, origin, world)) {
					possible.add(
						MultiblockInstance(
							UUID.randomUUID(),
							origin,
							world,
							facing,
							type,
						)
					)
				}
			}
		}
		// return the largest possible, in case there are multiple
		return possible.maxByOrNull { it.type.blocks.size }?.also {
			val chunk = world.bukkit.getChunkAt(origin.toLocation(world))
			multiblocks.getOrPut(chunk) { mutableSetOf() }.add(it)
			chunk.vanilla.isUnsaved = true
		}
	}


	@EventHandler
	private fun onPlayerAttemptDetect(event: PlayerInteractEvent) {
		if (event.action != Action.RIGHT_CLICK_BLOCK) return
		if (event.item?.let { ItemType.of(it) } != ItemType.of(DebugCustomItems.DETECTOR)) return
		getMultiblockAt(event.clickedBlock!!.toBlockPosition(), ServerWorld(event.player.world))
			?.let {
				event.player.sendRichActionBar("<dark_green>Found already detected ${it.type.displayName}")
				return
			}
		tryDetect(event.clickedBlock!!.toBlockPosition(), ServerWorld(event.player.world))?.let {
			event.player.sendRichActionBar("<green>Detected ${it.type.displayName}")
			return
		}
		event.player.sendRichActionBar("<red>No multiblock detected")
	}

	private val pdcKey = StellaricaServer.identifier("multiblocks").toNamespacedKey()

	@OptIn(ExperimentalSerializationApi::class)
	@EventHandler
	private fun onChunkLoad(event: ChunkLoadEvent) {
		val bytes = event.chunk.persistentDataContainer.get(pdcKey, PersistentDataType.BYTE_ARRAY) ?: return
		if (bytes.size <= 2) return // cursed hack, look into this!

		val pos = "(${event.chunk.x}, ${event.chunk.z})"
		try {
			if (multiblocks.containsKey(event.chunk))
				StellaricaServer.klogger.warn { "Chunk $pos already has multiblocks, which will be overwritten!" }

			multiblocks[event.chunk] = Cbor.decodeFromByteArray<MutableSet<MultiblockInstance>>(bytes);
		} catch (e: Exception) {
			e.printStackTrace()
			StellaricaServer.klogger.error { "Could not load multiblocks in chunk $pos" }
			multiblocks[event.chunk] = mutableSetOf()
		}
	}

	@OptIn(ExperimentalSerializationApi::class)
	@EventHandler
	private fun onChunkUnload(event: ChunkUnloadEvent) {
		val mb = multiblocks.remove(event.chunk) ?: mutableSetOf()
		try {
			event.chunk.persistentDataContainer.set(pdcKey, PersistentDataType.BYTE_ARRAY, Cbor.encodeToByteArray(mb))
		} catch (e: Exception) {
			e.printStackTrace()
			StellaricaServer.klogger.error { "Could not save multiblocks to chunk (${event.chunk.x}, ${event.chunk.z})" }
		}
	}
}
