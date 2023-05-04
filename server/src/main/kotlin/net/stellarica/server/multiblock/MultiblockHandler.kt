package net.stellarica.server.multiblock

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.stellarica.common.util.serializer.ResourceLocationSerializer
import net.stellarica.server.StellaricaServer.Companion.klogger
import net.stellarica.server.StellaricaServer.Companion.namespacedKey
import net.stellarica.server.material.custom.item.type.DebugCustomItems
import net.stellarica.server.material.type.item.ItemType
import net.stellarica.server.multiblock.data.MultiblockData
import net.stellarica.server.util.Tasks
import net.stellarica.server.util.extension.sendRichActionBar
import net.stellarica.server.util.extension.toBlockPos
import net.stellarica.server.util.extension.toLocation
import net.stellarica.server.util.extension.vanilla
import org.bukkit.Chunk
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.persistence.PersistentDataType
import java.util.UUID

object MultiblockHandler : Listener {
	internal val multiblocks = mutableMapOf<Chunk, MutableSet<MultiblockInstance>>()

	operator fun get(chunk: Chunk) = multiblocks.getOrPut(chunk) { mutableSetOf() }

	init {
		Tasks.syncRepeat(5, 20) {
			for ((_, mbSet) in multiblocks) {
				val invalid = mutableSetOf<MultiblockInstance>()
				for (multiblock in mbSet) {
					if (!multiblock.validate()) {
						invalid.add(multiblock)
					} else {
						multiblock.type.tick(multiblock)
					}
				}
				mbSet.removeAll(invalid)
			}
		}
	}

	fun detect(origin: BlockPos, world: World): MultiblockInstance? {
		val possible = mutableListOf<MultiblockInstance>()
		for (type in Multiblocks.all) {
			val instance = type.detect(origin, world)
			if (instance != null) {
				possible.add(instance)
			}
		}
		// return the largest possible, in case there are multiple
		return possible.maxByOrNull { it.type.blocks.size }?.also {
			val chunk = world.getChunkAt(origin.toLocation(world))
			multiblocks.getOrPut(chunk) { mutableSetOf() }.add(it)
			it.type.init(it)
			chunk.vanilla.isUnsaved = true
		}
	}

	// can't use multiblockinstance as we don't want to serialize the world or the type
	@Serializable
	private data class PersistentMultiblockData(
		val id: String,
		@Serializable(with = ResourceLocationSerializer::class)
		val type: ResourceLocation,
		val oX: Int,
		val oY: Int,
		val oZ: Int,
		val direction: Direction,
		val data: MultiblockData
	) {

		constructor(multiblock: MultiblockInstance) :
				this(
					multiblock.id.toString(),
					multiblock.type.id,
					multiblock.origin.x,
					multiblock.origin.y,
					multiblock.origin.z,
					multiblock.direction,
					multiblock.data
				)

		fun toInstance(world: World) =
			MultiblockInstance(
				UUID.fromString(id),
				BlockPos(oX, oY, oZ),
				world,
				direction,
				Multiblocks.byId(type)!!,
				data
			)
	}

	private fun loadFromChunk(chunk: Chunk) {
		chunk.persistentDataContainer.get(namespacedKey("multiblocks"), PersistentDataType.STRING)
			?.let { string ->
				Json.decodeFromString<Set<PersistentMultiblockData>>(string)
					.filter { it.type in Multiblocks.all.map { it.id } } // make sure it's a valid type still
					.map { it.toInstance(chunk.world) }
					.let { multiblocks.getOrPut(chunk) { mutableSetOf() }.addAll(it) }
			}
	}

	private fun saveToChunk(chunk: Chunk, force: Boolean = false) {
		if (!force && multiblocks[chunk]?.isEmpty() != false) return
		chunk.persistentDataContainer.set(
			namespacedKey("multiblocks"),
			PersistentDataType.STRING,
			Json.encodeToString(multiblocks[chunk]!!.map { PersistentMultiblockData(it) })
		)
	}

	@EventHandler
	fun onPlayerAttemptDetect(event: PlayerInteractEvent) {
		if (event.action != Action.RIGHT_CLICK_BLOCK) return
		if (event.item?.let { ItemType.of(it) } != ItemType.of(DebugCustomItems.DETECTOR)) return
		multiblocks[event.clickedBlock!!.chunk]?.firstOrNull { it.origin == event.clickedBlock!!.toBlockPos() }?.let {
			event.player.sendRichActionBar("<dark_green>Found already detected ${it.type.displayName}")
			return
		}
		detect(event.clickedBlock!!.toBlockPos(), event.player.world)?.let {
			event.player.sendRichActionBar("<green>Detected ${it.type.displayName}")
			return
		}
		event.player.sendRichActionBar("<red>No multiblock detected")
	}

	@EventHandler
	fun onChunkLoad(event: ChunkLoadEvent) {
		try {
			loadFromChunk(event.chunk)
		} catch (e: Exception) {
			klogger.error { "Failed to load multiblocks from chunk at ${event.chunk.x}, ${event.chunk.z}" }
			e.printStackTrace()
			klogger.error { "Clearing multiblock data for the chunk" }
			multiblocks[event.chunk] = mutableSetOf()
			saveToChunk(event.chunk, true)
		}
	}

	@EventHandler
	fun onChunkUnload(event: ChunkUnloadEvent) {
		saveToChunk(event.chunk)
		multiblocks.remove(event.chunk)
	}
}