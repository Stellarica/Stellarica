package net.stellarica.server.multiblocks

import com.destroystokyo.paper.event.server.ServerTickStartEvent
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.silkmc.silk.nbt.serialization.Nbt
import net.silkmc.silk.nbt.serialization.decodeFromNbtElement
import net.silkmc.silk.nbt.serialization.encodeToNbtElement
import net.stellarica.server.StellaricaServer
import org.bukkit.Chunk
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent

object MultiblockHandler: Listener {
	val types = mutableListOf<MultiblockType>()
	val multiblocks = mutableMapOf<Chunk, MutableSet<MultiblockInstance>>()

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
			if (MultiblockDetectEvent.call(MultiblockDetectEvent.EventData(it))) return null // maybe check for a smaller one?
			val chunk = world.getChunkAt(origin)
			MULTIBLOCKS.get(chunk).multiblocks.add(it)
			chunk.setNeedsSaving(true) // this should be moved to ChunkMultiblocksComponent
		}
	}


	var multiblocks = mutableSetOf<MultiblockInstance>()
		private set

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

		// validate multiblocks
		val invalid = mutableSetOf<MultiblockInstance>()
		multiblocks.forEach {
			if (!it.validate()) {
				MultiblockUndetectEvent.call(MultiblockUndetectEvent.EventData(it))
				invalid.add(it)
				chunk.setNeedsSaving(true)
			}
		}
		multiblocks.removeAll(invalid)
	}

	@EventHandler
	fun onChunkLoad(event: ChunkLoadEvent) {
		tag.get("multiblocks")?.let { nbt ->
			Nbt.decodeFromNbtElement<List<MultiblockData>>(nbt).map { it.toInstance(world) }
		}?.let { multiblocks.addAll(it) }
	}

	@EventHandler
	fun onChunkLoad(event: ChunkUnloadEvent) {
		if (multiblocks.size == 0) return
		tag.put("multiblocks", Nbt.encodeToNbtElement(multiblocks.map { MultiblockData(it) }))
	}
}