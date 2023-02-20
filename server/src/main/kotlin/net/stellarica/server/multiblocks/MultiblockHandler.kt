package net.stellarica.server.multiblocks

import com.destroystokyo.paper.event.server.ServerTickStartEvent
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.stellarica.server.StellaricaServer
import net.stellarica.server.StellaricaServer.Companion.namespacedKey
import net.stellarica.server.multiblocks.events.MultiblockDetectEvent
import net.stellarica.server.multiblocks.events.MultiblockUndetectEvent
import net.stellarica.server.utils.extensions.toLocation
import org.bukkit.Chunk
import org.bukkit.World
import org.bukkit.craftbukkit.v1_19_R2.CraftChunk
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.persistence.PersistentDataType

object MultiblockHandler: Listener {
	val types = mutableListOf<MultiblockType>()
	private val multiblocks = mutableMapOf<Chunk, MutableSet<MultiblockInstance>>()

	operator fun get(chunk: Chunk) = multiblocks.getOrDefault(chunk, mutableSetOf())

	fun detect(origin: BlockPos, world: World): MultiblockInstance? {
		val possible = mutableListOf<MultiblockInstance>()
		types.forEach {
			val instance = it.detect(origin, world)
			if (instance != null) {
				possible.add(instance)
			}
		}
		// return the largest possible, in case there are multiple
		return possible.maxByOrNull { it.type.blocks.size }?.also {
			if (!MultiblockDetectEvent(it).callEvent()) return null // maybe check for a smaller one?
			val chunk = world.getChunkAt(origin.toLocation(world))
			multiblocks.getOrDefault(chunk, mutableSetOf()).add(it)
			(chunk as CraftChunk).handle.isUnsaved = true
		}
	}

	// can't use multiblockinstance as we don't want to serialize the world or the type
	@Serializable
	private data class MultiblockData(
		val type: String,
		val oX: Int,
		val oY: Int,
		val oZ: Int,
		val direction: Direction
	) {

		constructor(multiblock: MultiblockInstance) :
				this(
					multiblock.type.id.path,
					multiblock.origin.x,
					multiblock.origin.y,
					multiblock.origin.z,
					multiblock.direction
				)

		fun toInstance(world: World) =
			MultiblockInstance(
				BlockPos(oX, oY, oZ),
				world,
				direction,
				StellaricaServer.identifier(type)
			)
	}



	@EventHandler
	fun onServerTick(event: ServerTickStartEvent) {
		// twice per second
		if (event.tickNumber % 10 != 0) return

		multiblocks.forEach { (_, mbSet) ->
			val invalid = mutableSetOf<MultiblockInstance>()
			mbSet.forEach {multiblock ->
				if (!multiblock.validate()) {
					MultiblockUndetectEvent(multiblock).callEvent()
					invalid.add(multiblock)
				}
			}
			mbSet.removeAll(invalid)
		}
	}

	@EventHandler
	fun onChunkLoad(event: ChunkLoadEvent) {
		event.chunk.persistentDataContainer.get(namespacedKey("multiblocks"), PersistentDataType.STRING)?.let {string ->
			Json.decodeFromString<Set<MultiblockData>>(string)
				.map { it.toInstance(event.world) }
				.let{ multiblocks.getOrDefault(event.chunk, mutableSetOf()).addAll(it) }
		}
	}

	@EventHandler
	fun onChunkUnload(event: ChunkUnloadEvent) {
		if (multiblocks[event.chunk]?.isEmpty() != false) return
		event.chunk.persistentDataContainer.set(
			namespacedKey("multiblocks"),
			PersistentDataType.STRING,
			Json.encodeToString(multiblocks[event.chunk]!!.map { MultiblockData(it) })
		)
	}
}